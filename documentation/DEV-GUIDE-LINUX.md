
# CloudUnit developement environment

You are reading the wright guide, if you want to setup an environment to contribute to CloudUnit development.
If you simply wish to test CloudUnit, you should maybe run our [Demo Vagrant box](DEMO-GUIDE.md).

## Requirements

* Linux Ubuntu/Debian
* Git / Java 1.8
* Node 5.x
* VirtualBox 5.0.4+ (www.virtualbox.org) - install before Vagrant
* Vagrant 1.7.8+ (www.vagrantup.com)
* Maven 3+ (maven.apache.org)

## Architecture sources

* `cloudunit/cu-manager`  : Maven project 
* `cloudunit/cu-plaform`  : Shell scripts for administration 
* `cloudunit/cu-services` : Docker images

## Dev Rules and Principles

* You have to configure a local dns (see further) to send any requests from your host to VM (IP fixed at 192.168.50.4) 
* A reverse proxy is included into vagrantbox to route the request (*.cloudunit.dev) to the right app.
* You use your favorite idea (intellij, Eclipse) to develop the maven project into 'cloudunit/cu-manager'.
* The backend is a spring application exposing a REST API
* The frontend is an AngularJS 1.x consuming the backend API
* You run the project with an embedded tomcat via maven tasks (tomcat:run). No need to install Tomcat locally.
* Mysql is included into vagrantbox so no need to install it locally.

## Installation 

### Local DNS

CloudUnit uses Docker and Java but others components. As pre-requisites, you need to install them to have a complete dev stack. You need to install a local DNS for entry.
```
Dnsmasq is a lightweight, easy to configure DNS forwarder 
and DHCP server […] is targeted at home networks[.]
```
You need to add a local DNS entry pointing to the vagrant IP address. More precisely, any address ending with admin.cloudunit.io shoud point to `192.168.50.4`. On Ubuntu, a simple way to achieve this is to install dnsmasq:
```
sudo apt-get install dnsmasq
sudo vi /etc/dnsmasq.conf
# Add the line: address=/.cloudunit.dev/192.168.50.4                      
sudo service dnsmasq restart
```

### How to install Vagrant plugins
```
vagrant plugin install vagrant-reload
vagrant plugin install vagrant-vbguest
```
### Source code installation

Follow these instructions :
```
sudo apt-get install nodejs npm
sudo ln -s "$(which nodejs)" /usr/bin/node
sudo npm install -g grunt grunt-cli bower 
cd $HOME && git clone https://github.com/Treeptik/cloudunit.git
cd $HOME/cloudunit/cu-manager/src/main/webapp && sudo npm install
```

## How to start Environment Developpment

1 - Start the vagrantbox and run Docker into Vagrant

```
$ cd $HOME/CloudUnit 
$ vagrant up
$ vagrant ssh 
cd cloudunit/cu-platform && ./reset-all.sh -y
```

2 - Run the UI for development (http://0.0.0.0:9000) from Linux

```
$ cd $HOME/CloudUnit/cu-manager/src/main/webapp && grunt serve
```

3 - Start the Java Backend from Linux

```
$ cd $HOME/CloudUnit/cu-manager
$ mvn clean compile tomcat7:run -DskipTests -Dspring.profiles.active=vagrant
```

# FAQ

All questions and answers about dev tasks

## How to reset Environment Development

```
vagrant ssh
cloudunit/cu-platform/reset-all.sh -y
```

## How to rebuild images

Update your sources, build the images and reninit the database :

```
$ vagrant ssh dev
$ cloudunit/cu-services/build-services.sh
$ cloudunit/cu-platform/reset-all.sh -y
```
