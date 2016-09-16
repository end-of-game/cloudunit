
# CloudUnit developpement environment

You are reading the wright guide, if you want to setup an environment to contribute to CloudUnit development.

## Requirements

* A MacOSX computer
* Vagrant 1.8+
* Git / Java 1.8
* Node 5.x
* VirtualBox 5.0.4+
* Maven 3
 
## Architecture for developpment

![Architecture Dev](img/plateforme-dev.png "Architecture Development")    

### General Rules

* You have to configure a local dns (see further) to send any requests from your host to VM (IP fixed at 192.168.50.4) 
* You use your favorite idea (intellij, Eclipse) to develop the maven project into 'cloudunit/cu-manager'.
* The backend is a spring application exposing a REST API
* The frontend is an AngularJS 1.4 consuming the backend API from Spring Java
* You run the project with an embedded tomcat via maven tasks (tomcat:run). No need to install Tomcat locally.
* Mysql is included into vagrantbox so no need to install it locally.

### Architecture sources

```
cloudunit/cu-manager  : Maven project 
cloudunit/cu-plaform  : Shell scripts for administration 
cloudunit/cu-services : Docker images
```

## Installation 

### Local DNS

CloudUnit uses Docker and Java but others components. 
As pre-requisites, you need to install them to have a complete dev stack. 
You need to install a local DNS for entry.
```
Dnsmasq is a lightweight, easy to configure DNS forwarder 
and DHCP server […] is targeted at home networks[.]
```
You need to add a local DNS entry pointing to the vagrant IP address. More precisely, any address ending with .cloudunit.dev shoud point to `192.168.50.4`. On Ubuntu, a simple way to achieve this is to install dnsmasq:

Update your homebrew installation
```
brew up
```

Install dnsmasq
```
brew install dnsmasq
```

Copy the configuration.
```
mkdir /usr/local/etc
cp /usr/local/opt/dnsmasq/dnsmasq.conf.example /usr/local/etc/dnsmasq.conf
```

Then edit the file `/usr/local/etc/dnsmasq.conf` and add the line:
```
address=/cloudunit.dev/192.168.50.4
```

Create the directory
```
sudo mkdir /etc/resolver
```
Then add the following file
```
sudo vi /etc/resolver/dev
vi /etc/resolver/dev
```
Add the following line
```
nameserver 127.0.0.1
```

Make a link with LaunchDaemons directory and then start dnsmasq
```
sudo cp -v $(brew --prefix dnsmasq)/homebrew.mxcl.dnsmasq.plist /Library/LaunchDaemons
sudo launchctl load -w /Library/LaunchDaemons/homebrew.mxcl.dnsmasq.plist
```
For more information in this environment, please read this [article](http://passingcuriosity.com/2013/dnsmasq-dev-osx/)

### Installation NPM

Mac Users are invited to follow the instructions given by the [npm website](https://nodejs.org)

### How to build Angular project

Follow these instructions

```
cd $HOME && git clone https://github.com/Treeptik/cloudUnit.git
cd $HOME/cloudUnit/cu-manager-ui && sudo npm install grunt -g
cd $HOME/cloudUnit/cu-manager-ui && sudo npm install grunt-cli -g
cd $HOME/cloudUnit/cu-manager-ui && sudo npm install bower -g
cd $HOME/cloudUnit/cu-manager-ui && sudo npm install -g n
cd $HOME/cloudUnit/cu-manager-ui && sudo n stable
cd $HOME/cloudUnit/cu-manager-ui && bower install
```

## How to install Vagrant plugins

```
vagrant plugin install vagrant-reload
vagrant plugin install vagrant-vbguest
```

## How to start Environment Developpment

1 - Start the vagrantbox and run Docker into Vagrant

```
cd $HOME/cloudUnit 
vagrant up
vagrant ssh 
cd cloudunit/cu-platform && ./reset.sh -y
```

2 - Run the UI for development (http://0.0.0.0:9000) from Mac

```
$ cd $HOME/cloudUnit/cu-manager-ui && grunt serve
```
>! **Issue** if you have the following issue
```
grunt-cli: The grunt command line interface (v1.2.0)
Fatal error: Unable to find local grunt.
```
Run the following command :
```
sudo npm update
```

3 - Start the Java Backend from Mac

```
cd $HOME/cloudUnit
mvn clean install -DskipTests
cd $HOME/cloudUnit/cu-manager
mvn clean compile tomcat7:run -DskipTests -Dspring.profiles.active=vagrant
```

You can use default password and login
```
login: johndoe
password: abc2015
```

## How to reset Environment Developpment

```
cd $HOME/cloudUnit
vagrant ssh dev
cloudunit/cu-platform/reset.sh -y
```

## How to run e2e test (selenium & protractor)

First of all, you have to you have to install Google Chrome.
Then, start the application in parallel.

```
cd $HOME/cloudunit/cu-manager-ui
grunt test
```
