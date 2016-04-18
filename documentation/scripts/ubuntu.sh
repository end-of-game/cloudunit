#!/bin/bash

mkdir install
cd install

sudo apt-get install ruby-full
sudo gem install compass

sudo apt-get install git
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default

curl -sL https://deb.nodesource.com/setup_5.x | sudo bash -
sudo apt-get install nodejs

NODE=$(node -v | cut -c1-2) 
VERSION='v5'

if [ $NODE != $VERSION ]
then
	echo 'Node can not be installed'
	exit 0
fi

wget http://apache.mirrors.ovh.net/ftp.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.zip
unzip apache-maven-3.3.9-bin.zip
export PATH=$HOME/install/apache-maven-3.x.y/bin:$PATH

wget http://download.virtualbox.org/virtualbox/debian vivid contrib
wget -q https://www.virtualbox.org/download/oracle_vbox.asc -O- | sudo apt-key add -
sudo apt-get update
sudo apt-get install virtualbox-5.0

wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.deb
sudo dpkg -i vagrant_1.8.1_x86_64.deb

sudo apt-get install dnsmasq
ADDRESS="address=/.cloudunit.dev/192.168.50.4"
sudo bash -c "echo $ADDRESS >> /etc/dnsmasq.conf"
sudo service dnsmasq restart

vagrant plugin install vagrant-reload
vagrant plugin install vagrant-vbguest

cd $HOME/install && git clone https://github.com/Treeptik/cloudunit.git

sudo ln -s "$(which nodejs)" /usr/bin/node
sudo npm install -g grunt grunt-cli bower 
cd $HOME/install/cloudunit/cu-manager/src/main/webapp && sudo npm install

cd $HOME/install/cloudunit/cu-vagrant
vagrant up
vagrant provision 
