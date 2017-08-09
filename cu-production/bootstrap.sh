#!/bin/bash

if [ -z "$1" ]; then
  export GIT_BRANCH=dev
  echo ""
  echo "Warning, you will clone and build the default branch : $GIT_BRANCH"
  echo ""
else
  if [ "$1" = "--silent" ] || [ "$1" = "-s" ]; then
    echo "SILENT_INSTALL=yes" >> .env
    export GIT_BRANCH=dev
  elif [ "$2" = "--silent" ] || [ "$2" = "-s" ]; then
    echo "SILENT_INSTALL=yes" >> .env
    export GIT_BRANCH=$1
  else
    export GIT_BRANCH=$1
  fi
fi

export CU_USER=admincu
export CU_HOME=/home/$CU_USER/cloudunit
export CU_INSTALL_DIR=$CU_HOME/cu-production

export COMPOSE_VERSION=1.9.0

MIN_DOCKER_VERSION=1.12

readonly ROOTUID="0"

install_docker() {
  # INSTALL DOCKER

  if [ "$distribution" = "Ubuntu" ]; then
    cp $CU_INSTALL_DIR/files/sources.list /etc/apt/sources.list
    apt-get install -y apt-transport-https ca-certificates
    apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
    apt-key fingerprint 0EBFCD88
    add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
       $(lsb_release -cs) \
       stable"
    apt-get -y update
    apt-get install -y docker-ce=17.06.0~ce-0~ubuntu
  else
    # documentation from https://docs.docker.com/cs-engine/1.12/
    rpm --import "https://sks-keyservers.net/pks/lookup?op=get&search=0xee6d536cf7dc86e2d7d56f59a178ac6c6238f52e"
    yum install -y yum-utils
    # yum list docker-engine.x86_64  --showduplicates |sort -r
    yum-config-manager -y --add-repo https://packages.docker.com/1.12/yum/repo/main/centos/7
    yum install -y docker-engine-1.12.6.cs12
    systemctl enable docker.service
  fi
  usermod -a -G docker $CU_USER

  if [ -n "$http_proxy" ]; then
    echo "Install HTTP proxy in docker daemon"
    echo "export http_proxy="$http_proxy >> /etc/default/docker
  fi

  if [ -n "$https_proxy" ]; then
    echo "Install HTTPS proxy in docker daemon"
    echo "export https_proxy="$https_proxy >> /etc/default/docker
  fi

  if [ -n "$ftp_proxy" ]; then
    echo "Install FTP proxy in docker daemon"
    echo "export ftp_proxy="$ftp_proxy >> /etc/default/docker
  fi

  if [ -n "$no_proxy" ]; then
    echo "Install no proxy in docker daemon"
    echo "export no_proxy="$no_proxy >> /etc/default/docker
  fi

  if [ "$distribution" = "Ubuntu" ]; then
    /etc/init.d/docker restart
  else
    systemctl restart docker.service
  fi
}

generate_certs() {
  service docker stop
  sh /home/"$CU_USER"/cloudunit/cu-production/generate-certs.sh
  if [ "$distribution" = "Ubuntu" ]; then
    cp -f $CU_INSTALL_DIR/files/docker.service /etc/default/docker
  else
    /bin/cp -rf $CU_INSTALL_DIR/files/docker.service.centos /lib/systemd/system/docker.service
    systemctl daemon-reload
  fi
  service docker start
}


check_root() {
    if [ "$(id -u)" -ne "$ROOTUID" ] ; then
        echo "This script must be executed with root privileges."
        exit 1
    fi
}

# check if the branch exists or not
check_git_branch() {
    if [ ! -f /usr/bin/git ]; then
      if [ "$distribution" = "Ubuntu" ]; then
        apt-get -y update
        apt-get install -y git
      else
        yum -y update
        yum -y install git
      fi
    fi

    BRANCH_EXIST=$(git ls-remote --heads https://github.com/Treeptik/cloudunit $GIT_BRANCH)
    echo "git ls-remote --heads https://github.com/Treeptik/cloudunit $GIT_BRANCH"
    if [ ! "$BRANCH_EXIST" ];
      then
        echo "The branch $1 is not valid. Please choose one the following branches: "
        git ls-remote --heads https://github.com/Treeptik/cloudunit
        exit 1
    fi
}

# CREATE ADMINCU USER admincu account
create_admincu_user() {
    groupadd -g 10000 $CU_USER
    if [ "$distribution" = "Ubuntu" ]; then
      useradd -m -u 10000 -g $CU_USER -G sudo -s /bin/bash $CU_USER
    else
      useradd -m -u 10000 -g $CU_USER -G wheel -s /bin/bash $CU_USER
    fi
    ls /home
}

install_dependencies() {
    # PROVISION THE ENV

    if [ "$distribution" = "Ubuntu" ]; then
      apt-get -y update
      apt-get install -y nmap
      apt-get install -y htop
      apt-get install -y ncdu
      apt-get install -y git
      apt-get install -y haveged
      apt-get install -y mysql-client
    else
      yum install epel-release -y
      yum -y update
      yum -y install nmap htop ncdu mariadb-devel git haveged
    fi
}

clone_project() {
    # CLONE CLOUDUNIT
    ls /home
    cd /home/$CU_USER && git clone https://github.com/Treeptik/cloudunit.git -b $GIT_BRANCH
    chown -R $CU_USER:$CU_USER /home/$CU_USER
}

install_docker_compose() {
    if [ "$distribution" = "Ubuntu" ]; then
      if [ ! -f /usr/local/bin/docker-compose ]; then
          curl -o docker-compose -L https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-`uname -s`-`uname -m`
          chmod a+x docker-compose
          mv docker-compose /usr/local/bin
       fi
    else 
      if [ ! -f /usr/bin/docker-compose ]; then
        curl -o docker-compose -L https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-`uname -s`-`uname -m`
        chmod a+x docker-compose
        mv docker-compose /usr/bin
      fi
    fi
}

install_log_rotation() {
    # Install log rotate
    cp $CU_INSTALL_DIR/files/docker-logrotate /etc/logrotate.d/
}

install_cron() {
    # Install cron restart
    mkdir -p /home/"$CU_USER"/.cloudunit
    cp $CU_INSTALL_DIR/files/cron.sh /home/"$CU_USER"/.cloudunit/cron.sh
    echo "*/3 * * * * $CU_USER /home/"$CU_USER"/.cloudunit/cron.sh" >> /etc/crontab
    chmod +x /home/"$CU_USER"/.cloudunit/cron.sh
}

override_rights() {
    chown -R $CU_USER /home/"$CU_USER"/
    chown -R $CU_USER /home/"$CU_USER"/.cloudunit
}

pull_images_from_dockerhub() {

  docker pull cloudunit/base-jessie:stable
  docker tag cloudunit/base-jessie:stable cloudunit/base-jessie:latest

  docker pull cloudunit/base-12.04:stable
  docker tag cloudunit/base-12.04:stable cloudunit/base-12.04:latest

  docker pull cloudunit/base-14.04:stable
  docker tag cloudunit/base-14.04:stable cloudunit/base-16.04:latest

  docker pull cloudunit/redis-3-2:stable
  docker tag cloudunit/redis-3-2:stable cloudunit/redis-3-2:latest

  docker pull cloudunit/manager:stable
  docker tag cloudunit/manager:stable cloudunit/manager:latest

  docker pull cloudunit/elk-kibana:stable
  docker tag cloudunit/elk-kibana:stable cloudunit/elk-kibana:latest

  docker pull cloudunit/elk-elasticsearch:stable
  docker tag cloudunit/elk-elasticsearch:stable cloudunit/elk-elasticsearch:stable

  docker pull cloudunit/jenkins:stable
  docker tag cloudunit/jenkins:stable cloudunit/jenkins:latest

  docker pull traefik:latest

  docker pull cloudunit/elk-monitoring-agents:stable
  docker tag cloudunit/elk-monitoring-agents:stable cloudunit/elk-monitoring-agents:stable

  docker pull cloudunit/tomcat-6:stable
  docker tag cloudunit/tomcat-6:stable cloudunit/tomcat-6:latest

  docker pull cloudunit/tomcat-7:stable
  docker tag cloudunit/tomcat-7:stable cloudunit/tomcat-7:latest

  docker pull cloudunit/tomcat-8:stable
  docker tag cloudunit/tomcat-8:stable cloudunit/tomcat-8:latest

  docker pull cloudunit/tomcat-85:stable
  docker tag cloudunit/tomcat-85:stable cloudunit/tomcat-85:latest

  docker pull cloudunit/tomcat-9:stable
  docker tag cloudunit/tomcat-9:stable cloudunit/tomcat-9:latest

  docker pull cloudunit/postgresql-9-3:stable
  docker tag cloudunit/postgresql-9-3:stable cloudunit/postgresql-9-3:latest

  docker pull cloudunit/postgresql-9-4:stable
  docker tag cloudunit/postgresql-9-4:stable cloudunit/postgresql-9-4:latest

  docker pull cloudunit/postgresql-9-5:stable
  docker tag cloudunit/postgresql-9-5:stable cloudunit/postgresql-9-5:stable

  docker pull cloudunit/fatjar:stable
  docker tag cloudunit/fatjar:stable cloudunit/fatjar:latest

  docker pull cloudunit/wildfly-8:stable
  docker tag cloudunit/wildfly-8:stable cloudunit/wildfly-8:latest

  docker pull cloudunit/wildfly-9:stable
  docker tag cloudunit/wildfly-9:stable cloudunit/wildfly-9:latest

  docker pull cloudunit/wildfly-10:stable
  docker tag cloudunit/wildfly-10:stable cloudunit/wildfly-10:latest

  docker pull cloudunit/apache-2-2:stable
  docker tag cloudunit/apache-2-2:stable cloudunit/apache-2-2:latest

  docker pull cloudunit/mysql-5-5:stable
  docker tag cloudunit/mysql-5-5:stable cloudunit/mysql-5-5:latest

  docker pull cloudunit/mysql-5-6:stable
  docker tag cloudunit/mysql-5-6:stable cloudunit/mysql-5-6:latest

  docker pull cloudunit/mysql-5-7:stable
  docker tag cloudunit/mysql-5-7:stable cloudunit/mysql-5-7:latest

  docker pull cloudunit/postgis-2-2:stable
  docker tag cloudunit/postgis-2-2:stable cloudunit/postgis-2-2:latest

  docker pull cloudunit/rabbitmq-3.6:stable
  docker tag cloudunit/rabbitmq-3.6:stable cloudunit/rabbitmq-3.6:latest

  docker pull cloudunit/activemq-5.13:stable
  docker tag cloudunit/activemq-5.13:stable cloudunit/activemq-5.13:latest

  docker pull cloudunit/elasticsearch-2.4:stable
  docker tag cloudunit/elasticsearch-2.4:stable cloudunit/elasticsearch-2.4:latest

  docker pull cloudunit/nginx-1.10:stable
  docker tag cloudunit/nginx-1.10:stable cloudunit/nginx-1.10:latest

}

question_pull_or_build() {
  if [[ -z "${METHOD}" ]]; then
    echo ""
    echo "Would you prefer to [build] or [pull] images (default is [build])"
    echo "( pull / build / continue ) : "
    read PUSHPULL
    if [ "$PUSHPULL" = "pull" ]; then
      logo_pulling_dockerhub
      pull_images_from_dockerhub
    elif [ "$PUSHPULL" = "build" -o "$PUSHPULL" = "" ]; then
      logo_building_cloudunit
      echo "image have been builded"
      cd /home/$CU_USER/cloudunit/cu-services && ./build-services.sh all
    elif [ "$PUSHPULL" = "continue" ]; then
      echo "No action. We will use current images"
    elif [ -n "$PUSHPULL" ]; then
      logo_building_cloudunit
      echo "image have been builded"
      cd /home/$CU_USER/cloudunit/cu-services && ./build-services.sh all
    else
      echo "Sorry, but I didn't understand your answer :("
      question_pull_or_build
    fi
  else
    if [ "$METHOD" = "pull" ]; then
      logo_pulling_dockerhub
      pull_images_from_dockerhub
    elif [ "$METHOD" = "build" ]; then
      logo_building_cloudunit
    fi
  fi
}

logo_building_cloudunit() {
 echo "Building CloudUnit..."
}

logo_pulling_dockerhub() {
echo "Pulling CloudUnit..."
}

question_proxy() {
	echo ""
	echo "Configure proxy (default is [no])"
	echo "( yes / no ) : "
	read USE_PROXY
	if [ "$USE_PROXY" = "yes" ]; then
    export USE_PROXY = $USE_PROXY
	  set_proxy
	else
	  echo "No configured proxy."
	fi
}

set_proxy() {
	echo ""
	echo "Enter proxy address : "
	read PROXY
	if [ -n "$PROXY" ]; then
		export http_proxy=$PROXY
		export https_proxy=$http_proxy
		export ftp_proxy=$http_proxy
    echo "Enter no proxy : "
    read PROXY_NO
    if [ -n "$PROXY_NO" ]; then
      export no_proxy=$PROXY_NO
    fi
	else
		echo "No configured proxy."
	fi
}

#
#
# MAIN
#
#

check_root

if [ -f /etc/redhat-release ]; then
  distribution=$(cat /etc/redhat-release | cut -c1-6)
else
  distribution=$(cat /etc/issue | cut -c1-6)
fi

if [ -n $http_proxy ] || [ -n $https_proxy ] || [ -n $ftp_proxy ] || [ -n $no_proxy ]; then
  echo "Proxy detected !"
else
  question_proxy
fi

check_git_branch

install_dependencies
create_admincu_user
clone_project

if [ -f /usr/bin/docker ]; then
  if [ "$(docker info | grep 'Server Version' | cut -c17-20)" = "$MIN_DOCKER_VERSION" ]; then
    echo "Docker version is good, continue"
  else
    echo "Docker version is not the right one lets upgrade"
    install_docker
  fi
else
  echo "Lets install docker from official repo"
  install_docker
fi

if [ ! -f /home/$CU_USER/.docker/ca.pem ]; then
  generate_certs
fi

install_docker_compose
install_log_rotation
install_cron

question_pull_or_build

override_rights

if [ -n "$SILENT_INSTALL" ] || [ "$SILENT_INSTALL" = "yes" ]; then
  cp -f .env /home/${CU_USER}/cloudunit/cu-compose
fi

cd /home/${CU_USER}/cloudunit/cu-compose
su $CU_USER -c "/bin/bash cu-docker-compose.sh with-elk"

echo "  ____ _                 _ _   _       _ _   "
echo " / ___| | ___  _   _  __| | | | |_ __ (_) |_ "
echo "| |   | |/ _ \| | | |/ _\` | | | | '_ \| | __|"
echo "| |___| | (_) | |_| | (_| | |_| | | | | | |_ "
echo " \____|_|\___/ \__,_|\__,_|\___/|_| |_|_|\__|"
echo "                           is up and running!"
