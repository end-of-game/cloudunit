#!/bin/bash

function install_java {
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
		echo "version is less than 1.8"
		exit 0
	    fi
	fi
}

function install_node {
	
	ISNODE=$(dpkg -s node | grep -e Status | head -n 1 | awk '{print $4}')
	if [ "$ISNODE" != "installed" ]
	then
		NODEVER=$(node -v | cut -c2)
		if (( $NODEVER == 5 )) 
		then
			echo "Node 5 already installed"
		else
			curl -sL https://deb.nodesource.com/setup_5.x | sudo bash -
			sudo apt-get -y install nodejs
			NODEVER=$(node -v | cut -c2)
			if (( $NODEVER < 5 ))
			then
				echo 'Node can not be installed'
				exit 0
			fi
		fi
	else
		curl -sL https://deb.nodesource.com/setup_5.x | sudo bash -
		sudo apt-get -y install nodejs
	fi
}

function install_maven {
	ISMAVEN=$(dpkg -s maven | grep -e Status | head -n 1 | awk '{print $4}')

	if [ "$ISMAVEN" != "installed" ]
	then 
		sudo apt-get -y update 
		sudo apt-get -y install maven
	else
		MAVENVER=$(dpkg -s maven | grep -e Version | head -n 1 | awk '{print $2}' | colrm 2)
		if (( $MAVENVER < 3 ))
		then
			sudo apt-get -y remove maven 
			sudo apt-get -y update 
			sudo apt-get -y install maven
		fi
	fi
}

function install_virtualbox {
	ISVIRTUALBOX=$(dpkg -s virtualbox | grep -e Status | head -n 1 | awk '{print $4}')

	if [ "$ISVIRTUALBOX" != "installed" ]
	then 
		sudo apt-get -y install virtualbox-qt
	else
		VIRTUALBOXVER=$(dpkg -s virtualbox | grep -e Version | head -n 1 | awk '{print $2}' | colrm 6)
		if ( (( "$(echo $VIRTUALBOXVER | cut -c1)" <= 5 )) && (( "$(echo $VIRTUALBOXVER | cut -c3)" <= 0 )) &&  (( "$(echo $VIRTUALBOXVER | cut -c5)" < 4)) )
		then
			sudo apt-get -y install virtualbox-qt
		fi
	fi
}

function install_vagrant {
	ISVAGRANT=$(dpkg -s vagrant | grep -e Status | head -n 1 | awk '{print $4}')

	if [ "$ISVAGRANT" != "installed" ]
	then 
		wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.deb
		sudo dpkg -i vagrant_1.8.1_x86_64.deb
	else
		VAGRANTVER=$(dpkg -s vagrant | grep -e Version | head -n 1  | awk '{print $2}' | cut -c3-5)
		if ( (( "$(echo $VAGRANTVER | cut -c1)" <= 1 )) && (( "$(echo $VAGRANTVER | cut -c3)" < 8 )) )
		then
			echo "Vagrant not install or not good version"		
			wget https://releases.hashicorp.com/vagrant/1.8.1/vagrant_1.8.1_x86_64.deb
			sudo dpkg -i vagrant_1.8.1_x86_64.deb
		fi
	fi
}

function install_dnsmasq {
	sudo apt-get -y install dnsmasq
	ADDRESS="address=/.cloudunit.dev/192.168.50.4"
	sudo bash -c "echo $ADDRESS >> /etc/dnsmasq.conf"
	sudo service dnsmasq restart
}

function install_vagrant_plugin {
	vagrant plugin install vagrant-reload
	vagrant plugin install vagrant-vbguest
}

function install_cloudunit {
	cd $HOME 
	git clone https://github.com/Treeptik/cloudunit.git

	sudo npm install -g grunt grunt-cli bower 
	cd $HOME/cloudunit/cu-manager/src/main/webapp
	sudo npm install 
	bower install

	cd $HOME/cloudunit/cu-vagrant

	if [ -d ".vagrant" ]
	then
		sudo rm -rf .vagrant
	fi

	vagrant up
	vagrant provision 
}

install_java
install_node
install_maven
install_virtualbox
install_vagrant
install_dnsmasq
install_vagrant_plugin
install_cloudunit
