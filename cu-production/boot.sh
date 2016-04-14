#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
export LC_ALL=en_US.UTF-8
export LANG=en_US.UTF-8
export LANGUAGE=en_US.UTF-8

readonly COMPOSE_VERSION=1.3.3

#==========================================================#
sudo apt-get update
sudo apt-get upgrade

cp -f cloudunit/cu-vagrant/files/profile /home/vagrant/.profile
source /home/vagrant/.profile
cp -f cloudunit/cu-vagrant/files/hosts /etc/hosts
cp -f cloudunit/cu-vagrant/files/environment /etc/environment
cp -f cloudunit/cu-vagrant/files/.bashrc /home/vagrant/.bashrc
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 36A1D7869245C8950F966E92D8576A8BA88D21E9
sudo cp -f cloudunit/cu-vagrant/files/sources.list /etc/apt/sources.list

sudo apt-get update

sudo apt-get install -y apt-transport-https
sudo apt-get install -y curl
sudo apt-get install -y vim
#sudo apt-get install -y golang

#==========================================================#

# clean up
sudo rm -f \
  /var/log/messages   \
  /var/log/lastlog    \
  /var/log/auth.log   \
  /var/log/syslog     \
  /var/log/daemon.log \
  /var/log/upstart/docker.log \
  /home/vagrant/.bash_history \
  /var/mail/vagrant           \
  || true

#==========================================================#

#
# Docker-related stuff
#
sudo apt-get update
sudo apt-get install -y linux-image-extra-$(uname -r)
sudo apt-get install -y lxc-docker-1.6.2 1.6.2
sudo apt-get install -y mysql-client
#sudo apt-mark hold lxc-docker
sudo usermod -aG docker vagrant

# stop docker
sudo service docker stop

# configure docker
sudo cp cloudunit/cu-vagrant/files/docker.no.secure.service  /etc/default/docker

# restart docker
sudo service docker start

# enable memory and swap accounting
sudo cp cloudunit/cu-vagrant/files/grub /etc/default/grub
sudo update-grub

# install Docker Compose
# @see http://docs.docker.com/compose/install/
curl -o docker-compose -L https://github.com/docker/compose/releases/download/$COMPOSE_VERSION/docker-compose-`uname -s`-`uname -m`
chmod a+x docker-compose
sudo mv docker-compose /usr/local/bin

# install Docker Machine
# @see https://docs.docker.com/machine/
#curl -L https://github.com/docker/machine/releases/download/$MACHINE_VERSION/docker-machine-`uname -s`-`uname -m` > docker-machine
#chmod a+x docker-machine
#sudo mv docker-machine /usr/local/bin/

# install swarm
#sudo docker pull swarm

# install docker-bench-security
#docker pull diogomonica/docker-bench-security
#sudo cp cloudunit/cu-vagrant/files/docker-bench-security /usr/local/bin
#chmod a+x /usr/local/bin/docker-bench-security

# to activate the new docker.service file configuration
#sudo service docker stop
#sudo service docker start

cd /home/vagrant/cloudunit/cu-services && ./build-services.sh
cd /home/vagrant/cloudunit/cu-platform && su -l vagrant -c "/home/vagrant/cloudunit/cu-platform/reset-all.sh -y"
