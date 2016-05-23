#!/bin/bash

export CU_USER=admincu
export CU_HOME=/home/$CU_USER/cloudunit
export CU_INSTALL_DIR=$CU_HOME/cu-production

# INIT
apt-get update

# CREATE ADMINCU USER admincu account
useradd -m $CU_USER
usermod $CU_USER -aG sudo

# PROVISION THE ENV
apt-get install -y git
apt-get install -y mysql-client

# CLONE CLOUDUNIT
cd /home/$CU_USER
git clone https://github.com/Treeptik/cloudunit.git
cp -f $CU_INSTALL_DIR/files/sudoers /etc/sudoers
chown -R $CU_USER:$CU_USER /home/$CU_USER

# INSTALL DOCKER
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
cp $CU_INSTALL_DIR/files/sources.list /etc/apt/sources.list
apt-get update
apt-get install -y lxc-docker-1.6.2 1.6.2
apt-mark hold lxc-docker
apt-get install -y linux-image-extra-$(uname -r)
usermod -aG docker $CU_USER
curl -L https://github.com/docker/compose/releases/download/1.3.3/docker-compose-`uname -s`-`uname -m` > docker-compose
chmod +x docker-compose
mv docker-compose /usr/local/bin

# INSTALL CERTIFICATS
cp $CU_INSTALL_DIR/files/docker.secure /etc/default/docker
mkdir -p /root/.docker
cp $CU_HOME/conf/cert/server/* /root/.docker
mkdir -p /home/$CU_USER/.docker
cp $CU_HOME/conf/cert/server/* /home/admincu/.docker/
chown -R $CU_USER:$CU_USER /home/$CU_USER/.docker
cp $CU_INSTALL_DIR/files/environment /etc/environment
cp $CU_INSTALL_DIR/files/hosts /etc/hosts
service docker stop
sleep 5
service docker start

# BUILD SERVICES
su -l $CU_USER -c "cd $CU_HOME/cu-services && ./build-services.sh"

# COMPILE ROOT WAR FOR CLOUDUNIT
cd $CU_HOME/cu-manager
./compile-root-war.sh
cp target/ROOT.war $CU_HOME/cu-production/tomcat
chown -R $CU_USER:$CU_USER /home/$CU_USER/.docker

# RESET ALL FOR FIRST START
su -l $CU_USER -c "cd $CU_HOME/cu-production && ./reset-all.sh -y"



