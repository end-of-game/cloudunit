#!/bin/bash

export CU_USER=admincu
export CU_HOME=/home/$CU_USER/cloudunit
export CU_INSTALL_DIR=$CU_HOME/cu-production

export COMPOSE_VERSION=1.9.0

MIN_DOCKER_VERSION=1.12

ROOTUID="0"

intall_docker() {
  # INSTALL DOCKER
  apt-get install -y apt-transport-https ca-certificates
  apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
  cp $CU_INSTALL_DIR/files/sources.list /etc/apt/sources.list
  apt-get install -y docker-engine
  service docker stop
  sh /home/"$CU_USER"/cloudunit/cu-production/generate-certs.sh
  cp -f $CU_INSTALL_DIR/files/docker.service /etc/default/docker
  service docker start
}

if [ "$(id -u)" -ne "$ROOTUID" ] ; then
    echo "This script must be executed with root privileges."
    exit 1
fi

# INIT
apt-get update
if [ ! -f /usr/bin/git ]; then
  apt-get install -y git
fi

if [ -z "$1" ]; then
  echo "No branch argument supplied so dev will be used"
  GIT_BRANCH=dev
fi

BRANCH_EXIST=$(git ls-remote --heads https://github.com/Treeptik/cloudunit $GIT_BRANCH)
echo git ls-remote --heads https://github.com/Treeptik/cloudunit $GIT_BRANCH
if [ ! "$BRANCH_EXIST" ];
  then
    echo "The branch $1 is not valid. Please choose one the following branches: "
    git ls-remote --heads https://github.com/Treeptik/cloudunit
    exit 1
fi

# CREATE ADMINCU USER admincu account
groupadd -g 10000 $CU_USER
useradd -m -u 10000 -s /bin/bash $CU_USER
usermod -a -G docker,sudo,$CU_USER

# PROVISION THE ENV
apt-get install -y nmap
apt-get install -y htop
apt-get install -y ncdu
apt-get install -y git
apt-get install -y haveged

# CLONE CLOUDUNIT
cd /home/$CU_USER && git clone https://github.com/Treeptik/cloudunit.git -b $GIT_BRANCH
chown -R $CU_USER:$CU_USER /home/$CU_USER

apt-get install -y mysql-client

if [ -f /usr/bin/docker ]; then
  if [ "$(docker info | grep 'Server Version' | cut -c17-20)" = "$MIN_DOCKER_VERSION" ]; then
    echo "Docker version is good, continue"
  else
    echo "Docker version is not the right one lets upgrade"
    intall_docker
  fi
else
  echo "Lets install docker from official repo"
  intall_docker
fi

if [ ! -f /usr/local/bin/docker-compose ]; then
  # install Docker Compose
  # @see http://docs.docker.com/compose/install/
  curl -o docker-compose -L https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-`uname -s`-`uname -m`
  chmod a+x docker-compose
  sudo mv docker-compose /usr/local/bin
fi

# Install log rotate
cp $CU_INSTALL_DIR/files/docker-logrotate /etc/logrotate.d/

# Install cron restart
mkdir -p /home/"$CU_USER"/.cloudunit
cp $CU_INSTALL_DIR/files/cron.sh /home/"$CU_USER"/.cloudunit/cron.sh
echo "*/3 * * * * admincu /home/"$CU_USER"/.cloudunit/cron.sh" >> /etc/crontab

chmod +x /home/"$CU_USER"/.cloudunit/cron.sh
chown -R $CU_USER /home/"$CU_USER"/
chown -R $CU_USER /home/"$CU_USER"/.cloudunit

# Add admincu to sudoers group
cp -f $CU_INSTALL_DIR/files/sudoers /etc/sudoers
usermod -g sudo $CU_USER

# copy the environment file
cp -f $CU_INSTALL_DIR/files/environment /etc/environment
sed -i "s/DOMAIN_NAME/$domain/g" /etc/environment

# display values to declare
echo ""
echo "You have to declare into your dns"
echo ""
cat /etc/environment

# Change admincu passwd
passwd $CU_USER

echo ""
echo "Lets get application images"
echo ""

echo "Would you prefer to build or pull images [default is pull]"
read PUSHPULL
if [ "$PUSHPULL" = "" ] || [ "$PUSHPULL" == "pull" ]; then
  PUSHPULL="pull"
  echo "Lets pull all image go take a cofee"
  docker pull cloudunit/tomcat-8
  docker pull cloudunit/postgresql-9-3
elif [ "$PUSHPULL" = "build" ]; then
  cd /home/$CU_USER/cloudunit/cu-services && ./build-services.sh all
else
  echo "I didn't understand you response"
  exit 1
fi

echo ""
echo "Lets start cloudunit"
echo ""

cd /home/$CU_USER/cloudunit/cu-compose
sh $CU_USER -c "./cu-docker-compose.sh with-elk"
