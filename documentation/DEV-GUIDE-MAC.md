
# CloudUnit developpement environment

You are reading the wright guide, if you want to setup an environment to contribute to CloudUnit development.
If you simply wish to test CloudUnit, you should maybe run our [Demo Vagrant box](https://github.com/Treeptik/CloudUnit/blob/master/DEMO-GUIDE.md), if you want to setup a CloudUnit server, in order to frequently use it read our [Server guide](https://github.com/Treeptik/cloudunit/blob/master/SERVER-GUIDE.md).

## Requirements

* A MacOSX computer
* Vagrant 1.7.8+
* VirtualBox 5.0.4+
* Maven 3
 
## Architecture sources

* `cloudunit/cu-manager`  : Maven project 
* `cloudunit/cu-plaform`  : Shell scripts for administration 
* `cloudunit/cu-services` : Docker images

## Dev Rules

* You have to configure a local dns (see further) to send any requests from your host to VM (IP fixed at 192.168.50.4) 
* A reverse proxy is included into vagrantbox to route the request (*.cloudunit.dev) to the right app.
* You use your favorite idea (intellij, Eclipse) to develop the maven project into 'cloudunit/cu-manager'.
* The backend is a spring application exposing a REST API
* The frontend is an AngularJS 1.x consuming the backend API
* You run the project with an embedded tomcat via maven tasks (tomcat:run). No need to install Tomcat locally.
* Mysql is included into vagrantbox so no need to install it locally.
* Ansible is needed locally to provision Vagrantbox (it will disapear in a near future...)

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

Finally, start dnsmasq:
```
$ sudo launchctl start homebrew.mxcl.dnsmasq
```
For more information in this environment, please read this [article](http://passingcuriosity.com/2013/dnsmasq-dev-osx/)

### Installation NPM

Mac Users are invited to follow the instructions given by the [npm website](https://nodejs.org)

### Source code installation

Follow these instructions
* `mkdir $HOME/infrastructure`
* `cd $HOME/infrastructure`
* `git clone https://github.com/Treeptik/CU-infrastructure`
* `cd $HOME && git clone https://github.com/Treeptik/CloudUnit.git`
* `cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install grunt -g`
* `cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install grunt-cli -g`
* `cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install bower -g`
* `cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo npm install -g n`
* `cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo stable n`
* `cd $HOME/CloudUnit/cu-manager/src/main/webapp && bower install`
* `cd $HOME/CloudUnit/cu-manager/src/main/webapp && sudo gem install compass`

## How to install Vagrant plugins
```
vagrant plugin install vagrant-reload
vagrant plugin install vagrant-vbguest
```

## How to start Environment Developpment

To run the UI for development (http://0.0.0.0:9000)
*  `cd $HOME/CloudUnit/cu-manager/src/main/webapp && vagrant up dev`
*  `cd $HOME/CloudUnit/cu-manager/src/main/webapp && grunt serve`
*  `cd $HOME/CloudUnit/cu-manager`
*  `mvn clean compile tomcat7:run -DskipTests -Dspring.profiles.active=vagrant`

## How to reset Environment Developpment

```
cd $HOME/CloudUnit
vagrant ssh dev
cloudunit/cu-platform/reset-all.sh -y
```

## Docker daemon Socket Configuration

By default, Docker is secured by TLS. We provide default certificates to run cloudunit manager in dev and demo modes.
You must set the certificates path into src/resources/application-vagrant.properties :
If you want to start Docker daemon in http mode (if you have a MAC OSX, TLS is not supported so you have to do it for
dev mode), enter the vagrant box and :

```bash
sudo service docker stop
```

Comment this line into `/etc/default/docker`
```
#DOCKER_OPTS="--bip 172.17.42.1/16 --dns 172.17.42.1 --dns 8.8.8.8 -H unix:///var/run/docker.sock -g /usr/local/docker
-s aufs --tlsverify --tlscacert=/root/.docker/ca.pem --tlscert=/root/.docker/server-cert.pem
--tlskey=/root/.docker/server-key.pem  -H=0.0.0.0:2376"
```

Then uncomment this other line :

```
#DOCKER_OPTS="--bip 172.17.42.1/16 --dns 172.17.42.1 --dns 8.8.8.8 -H tcp://0.0.0.0:4243
-H unix:///var/run/docker.sock -g /usr/local/docker -s aufs"
```

- restart Docker daemon
```bash
sudo service docker start
```

