#!/bin/bash

if [ -z "$1" ]; then
  export GIT_BRANCH=dev
  echo ""
  echo "Warning, you will clone and build the default branch : $GIT_BRANCH"
  echo ""
else
  export GIT_BRANCH=$1
fi

export CU_USER=admincu
export CU_HOME=/home/$CU_USER/cloudunit
export CU_INSTALL_DIR=$CU_HOME/cu-production

export COMPOSE_VERSION=1.9.0

MIN_DOCKER_VERSION=1.12

readonly ROOTUID="0"

install_docker() {
  # INSTALL DOCKER
  cp $CU_INSTALL_DIR/files/sources.list /etc/apt/sources.list
  apt-get install -y apt-transport-https ca-certificates
  apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
  apt-get update
  apt-get install -y docker-engine
  usermod -a -G docker $CU_USER
}

generate_certs() {
  service docker stop
  sh /home/"$CU_USER"/cloudunit/cu-production/generate-certs.sh
  cp -f $CU_INSTALL_DIR/files/docker.service /etc/default/docker
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
        apt-get update
        apt-get install -y git
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
    useradd -m -u 10000 -g $CU_USER -G sudo -s /bin/bash $CU_USER
    ls /home
}

install_dependencies() {
    # PROVISION THE ENV
    apt-get update
    apt-get install -y nmap
    apt-get install -y htop
    apt-get install -y ncdu
    apt-get install -y git
    apt-get install -y haveged
    apt-get install -y mysql-client
}

clone_project() {
    # CLONE CLOUDUNIT
    ls /home
    cd /home/$CU_USER && git clone https://github.com/Treeptik/cloudunit.git -b $GIT_BRANCH
    chown -R $CU_USER:$CU_USER /home/$CU_USER
}

install_docker_compose() {
    if [ ! -f /usr/local/bin/docker-compose ]; then
      curl -o docker-compose -L https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-`uname -s`-`uname -m`
      chmod a+x docker-compose
      sudo mv docker-compose /usr/local/bin
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

add_user_to_sudoers() {
    # Add user to sudoers group
    cp -f $CU_INSTALL_DIR/files/sudoers /etc/sudoers
}

pull_images_from_dockerhub() {
  docker pull cloudunit/tomcat-6
  docker pull cloudunit/tomcat-7
  docker pull cloudunit/tomcat-8
  docker pull cloudunit/tomcat-85
  docker pull cloudunit/tomcat-9
  docker pull cloudunit/postgresql-9-3
  docker pull cloudunit/postgresql-9-4
  docker pull cloudunit/postgresql-9-5
  docker pull cloudunit/fatjar
  docker pull cloudunit/wildfly-8
  docker pull cloudunit/wildfly-9
  docker pull cloudunit/wildfly-10
  docker pull cloudunit/apache-2-2
  docker pull cloudunit/mysql-5-5
  docker pull cloudunit/mysql-5-6
  docker pull cloudunit/mysql-5-7
  docker pull cloudunit/postgis-2-2
  docker pull cloudunit/rabbitmq-3.6
  docker pull cloudunit/activemq-5.13
  docker pull cloudunit/elasticsearch-2.4
}

question_pull_or_build() {
    echo ""
    echo "Would you prefer to [build] or [pull] images (default is [pull])"
    echo "( pull / build / continue ) : "
    read PUSHPULL
    if [ "$PUSHPULL" = "pull" ]; then
      logo_pulling_dockerhub
      pull_images_from_dockerhub
    elif [ "$PUSHPULL" = "build" ]; then
      logo_building_cloudunit
      echo "image have been builded"
      cd /home/$CU_USER/cloudunit/cu-services && ./build-services.sh all
    elif [ "$PUSHPULL" = "continue" ]; then
      echo "No action... we use current images"
    else
      echo "Sorry but I didn't understand you response..."
      question_pull_or_build
    fi
}

logo_building_cloudunit() {
 echo " ____        _ _     _ _                ____ _                 _ _   _       _ _ "
 echo "| __ ) _   _(_) | __| (_)_ __   __ _   / ___| | ___  _   _  __| | | | |_ __ (_) |_ "
 echo "|  _ \| | | | | |/ _\` | | '_ \ / _\` | | |   | |/ _ \| | | |/ _\` | | | | '_ \| | __| "
 echo "| |_) | |_| | | | (_| | | | | | (_| | | |___| | (_) | |_| | (_| | |_| | | | | | |_ "
 echo "|____/ \__,_|_|_|\__,_|_|_| |_|\__, |  \____|_|\___/ \__,_|\__,_|\___/|_| |_|_|\__| "
 echo "                                |___/"
}

logo_pulling_dockerhub() {
 echo "____        _ _ _               ____             _             _   _       _"
 echo "|  _ \ _   _| | (_)_ __   __ _  |  _ \  ___   ___| | _____ _ __| | | |_   _| |__"
 echo "| |_) | | | | | | | '_ \ / _\` | | | | |/ _ \ / __| |/ / _ \ '__| |_| | | | | '_ \ "
 echo "|  __/| |_| | | | | | | | (_| | | |_| | (_) | (__|   <  __/ |  |  _  | |_| | |_) | "
 echo "|_|    \__,_|_|_|_|_| |_|\__, | |____/ \___/ \___|_|\_\___|_|  |_| |_|\__,_|_.__/ "
 echo "                          |___/"
}

#
#
# MAIN
#
#

check_root
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
add_user_to_sudoers

cd /home/${CU_USER}/cloudunit/cu-compose
su $CU_USER -c "/bin/bash cu-docker-compose.sh with-elk"

echo "#"
echo "#"
echo "# CloudUnit is started but don't forget to set a password to $CU_USER"
echo "# Command : passwd $CU_USER"
echo "#"
echo "#"

