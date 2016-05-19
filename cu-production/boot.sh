#!/bin/bash

export CU_HOME=/home/admincu/cloudunit
export CU_INSTALL_DIR=$CU_HOME/cu-production

function init {
 apt-get update
}

# install admincu account
function create_admincu {
  useradd -m admincu
  usermod admincu -aG sudo
  cp -f files/sudoers /etc/sudoers
}

# clone the project
function clone_cloudunit {
  cd /home/admincu
  git clone https://github.com/Treeptik/cloudunit.git 
  chown -R admincu:admincu /home/admincu
}

# prepare the environment
function provision_packages {
 apt-get install -y git
 apt-get install -y mysql-client
}

# install docker 
function provision_docker {
  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9 
  cp $CU_INSTALL_DIR/files/sources.list /etc/apt/sources.list
  apt-get update
  apt-get install -y lxc-docker-1.6.2 1.6.2
  apt-mark hold lxc-docker
  apt-get install -y linux-image-extra-$(uname -r)
  usermod -aG docker admincu

  curl -L https://github.com/docker/compose/releases/download/1.3.3/docker-compose-`uname -s`-`uname -m` > docker-compose
  chmod +x docker-compose
  mv docker-compose /usr/local/bin
}

# install certificats for docker engine and client
function install_certs {
  cp $CU_INSTALL_DIR/files/docker.secure /etc/default/docker

  mkdir -p /root/.docker
  cp /home/admincu/cloudunit/conf/cert/server/* /root/.docker

  mkdir -p /home/admincu/.docker
  cp /home/admincu/cloudunit/conf/cert/server/* /home/admincu/.docker/
  chown -R admincu:admincu /home/admincu/.docker

  cp $CU_INSTALL_DIR/files/environment /etc/environment
  cp $CU_INSTALL_DIR/files/hosts /etc/hosts

  service docker stop
  sleep 5
  service docker start
}

function build_cloudunit {
  su -l admincu -c "cd /home/admincu/cloudunit/cu-services && ./build-services.sh"
}

function compile_war {
  cd /home/admincu/cloudunit/cu-manager
  ./compile-root-war.sh
  cp target/ROOT.war /home/admincu/cloudunit/cu-production/tomcat
  chown -R admincu:admincu /home/admincu/.docker
}

function start_cloudunit {
  su -l admincu -c "cd /home/admincu/cloudunit/cu-production && ./reset-all.sh -y"
}

# ------------------------------

create_admincu
provision_packages
clone_cloudunit
provision_docker
install_certs
build_cloudunit
compile_war
start_cloudunit


