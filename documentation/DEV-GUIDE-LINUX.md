
# CloudUnit developpement environment

You are reading the wright guide, if you want to setup an environment to contribute to CloudUnit development.
If you simply wish to test CloudUnit, you should maybe run our [Demo Vagrant box](DEMO-GUIDE.md).

## Requirements

* Linux Ubuntu/Debian 
* Vagrant 1.7.8+ (www.vagrantup.com)
* VirtualBox 5.0.4+ (www.virtualbox.org)
* Maven 3+ (maven.apache.org)
* Ansible (see further for installation)

## Architecture Source

* `cloudunit/cu-manager`  : Maven project 
* `cloudunit/cu-plaform`  : Shell scripts for administration 
* `cloudunit/cu-services` : Docker images

## Dev Rules

* You have to configure a DNS to send any requests from your host to VM (IP fixed at 192.168.50.4).
* A reverse proxy is included into vagrantbox to route the request (*.cloudunit.dev) to the right app.
* You use your favorite idea (intellij, Eclipse) to develop the maven project into 'cloudunit/cu-manager'.
* You run the project with an embedded tomcat via maven tasks (tomcat:run). No need to install Tomcat locally.
* Mysql is included into vagrantbox so no need to install it locally.
* Ansible is needed locally to provision Vagrantbox (it will disapear in a near future...)

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
vi /etc/dnsmasq.conf
# Add the line: address=/.cloudunit.dev/192.168.50.4                      
sudo service dnsmasq restart
```

### How to install Ansible 1.9+ if needed

```
sudo apt-get install software-properties-common
sudo apt-add-repository ppa:ansible/ansible
sudo apt-get update
sudo apt-get install ansible
```

### How to install Vagrant plugins
```
vagrant plugin install vagrant-reload
vagrant plugin install vagrant-vbguest
```
### Source code installation

Follow these instructions : 
```
mkdir $HOME/infrastructure
cd $HOME/infrastructure
git clone https://github.com/Treeptik/CU-infrastructure
cd $HOME && git clone https://github.com/Treeptik/CloudUnit.git
cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install grunt -g
cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install grunt-cli -g
cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install bower -g
cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install -g n
cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo stable n
cd $HOME/CloudUnit/cu-manager/src/main/webapp && bower install
cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo gem install compass
```

## How to start Environment Developpment

To run the UI for development (http://0.0.0.0:9000)
```
cd $HOME/CloudUnit/cu-manager/src/main/webapp && vagrant up dev
cd $HOME/CloudUnit/cu-manager/src/main/webapp && grunt serve
cd $HOME/CloudUnit/cu-manager
mvn clean compile tomcat7:run -DskipTests -Dspring.profiles.active=vagrant
```

## How to reset Environment Developpment

```
vagrant ssh dev
cloudunit/cu-platform/reset-all.sh -y
```

