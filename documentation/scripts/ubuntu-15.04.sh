#!/bin/bash

###### Java #######
if type -p java; then
	echo found java executable in PATH
	_java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
	echo found java executable in JAVA_HOME     
	_java="$JAVA_HOME/bin/java"
else
	echo "no java"
	exit 0
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" < "1.8" ]]; then
	echo version is less than 1.8
	exit 0
    fi
fi

###### Node #######
curl -sL https://deb.nodesource.com/setup_5.x | sudo bash -
sudo apt-get -y install nodejs
NODE=$(node -v | cut -c1-2) 
VERSION='v5'
if [ $NODE != $VERSION ]
then
	echo 'Node can not be installed'
	exit 0
fi

###### Maven #######
sudo apt-get -y remove maven && sudo apt-get -y update && sudo apt-get -y install maven

###### VirtualBox #######
ISVIRTUALBOX = $(hash virtualbox)
if($ISVIRTUALBOX)
then
	wget http://download.virtualbox.org/virtualbox/debian vivid contrib
	wget -q https://www.virtualbox.org/download/oracle_vbox.asc -O- | sudo apt-key add -
	sudo apt-get -y update
	sudo apt-get -y install virtualbox-5.0
else
	VIRTUALBOX = $(virtualbox --help | head -n 1 | awk '{print $NF}')
	echo $VIRTUALBOX
	if($VIRTUALBOX[0] < 5)
	then
		echo 'VirtualBox is less than 5.x'
		wget http://download.virtualbox.org/virtualbox/debian vivid contrib
		wget -q https://www.virtualbox.org/download/oracle_vbox.asc -O- | sudo apt-key add -
		sudo apt-get -y update
		sudo apt-get -y install virtualbox-5.0
	fi
fi

###### Vagrant #######
ISVAGRANT = $(hash vagrant)
if($ISVAGRANT)
then
	wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.deb
	sudo dpkg -i vagrant_1.8.1_x86_64.deb
else
	VAGRANT = $(vagrant -v | tail -c 6)
	if($VAGRANT[0] <= 1 && $VAGRANT[2] < 8)
	then
		wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.deb
		sudo dpkg -i vagrant_1.8.1_x86_64.deb
	fi
fi


source $HOME/.bashrc

sudo apt-get -y install dnsmasq
ADDRESS="address=/.cloudunit.dev/192.168.50.4"
sudo bash -c "echo $ADDRESS >> /etc/dnsmasq.conf"
sudo service dnsmasq restart

vagrant plugin install vagrant-reload
vagrant plugin install vagrant-vbguest

cd $HOME && git clone https://github.com/Treeptik/cloudunit.git

sudo ln -s "$(which nodejs)" /usr/bin/node
sudo npm install -g grunt grunt-cli bower 
cd $HOME/cloudunit/cu-manager/src/main/webapp && sudo npm install

cd $HOME/cloudunit/cu-vagrant
vagrant up
vagrant provision 
