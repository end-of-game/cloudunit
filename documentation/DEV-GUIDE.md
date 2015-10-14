
# CloudUnit developpement environment

You are reading the wright guide, if you want to setup an environment to contribute to CloudUnit development.
If you simply wish to test CloudUnit, you should maybe run our [Demo Vagrant box](https://github.com/Treeptik/CloudUnit/blob/master/DEMO-GUIDE.md), if you want to setup a CloudUnit server, in order to frequently use it read our [Server guide](https://github.com/Treeptik/cloudunit/blob/master/SERVER-GUIDE.md).

## Requirements

* A linux Ubuntu/Debian or MacOSX computer. Windows is not tested yet. 
* Vagrant 1.7.8+
* VirtualBox 5.0.4+
* Npm 4.x+ for Grunt and Bower stack

## Installation 

CloudUnit uses Docker and Java but others components. As pre-requisites, you need to install them to have a complete dev stack.

You need to install a local DNS for entry.
```
Dnsmasq is a lightweight, easy to configure DNS forwarder and DHCP server […] is targeted at home networks[.]
```

### Local DNS (Linux Debian)

You need to add a local DNS entry pointing to the vagrant IP address. More precisely, any address ending with admin.cloudunit.io shoud point to `192.168.50.4`. On Ubuntu, a simple way to achieve this is to install dnsmasq:
```
$ sudo apt-get install dnsmasq
```
Then edit the file `/etc/dnsmasq.conf` and add the line:
```
address=/.cloudunit.dev/192.168.50.4
```
Finally, restart dnsmasq:
```
$ sudo service dnsmasq restart
```

### Local DNS (MacOSX)

You need to add a local DNS entry pointing to the vagrant IP address. More precisely, any address ending with .cloudunit.dev shoud point to `192.168.50.4`. On Ubuntu, a simple way to achieve this is to install dnsmasq:
```
# Update your homebrew installation
brew up
# Install dnsmasq
brew install dnsmasq
```
Then edit the file `/usr/local/etc/dnsmasq.conf` and add the line:
```
address=/.cloudunit.dev/192.168.50.4
```
Finally, start dnsmasq:
```
$ sudo launchctl start homebrew.mxcl.dnsmasq
```
For more information in this environment, please read this [article](http://passingcuriosity.com/2013/dnsmasq-dev-osx/)

### Installation NPM

```
sudo apt-get install npm
sudo npm install -g n
```
Mac Users are invited to follow the instructions given by the [npm website](https://nodejs.org)

### Installation SaSS / Compass
```
sudo apt-get install ruby-full
sudo gem install sass compass
```

### Installation Grunt
```
sudo npm install -g grunt grunt-cli
```

### Installation local extensions
```
Go to directory :  cu-manager/scr/main/webapp
npm install
bower install
```

## Commands

### Grunt 

To run the UI for development (http://0.0.0.0:9000)
* To run the projet : `grunt serve`

Else
* To build the projet : `grunt build`
* To run the E2E tests : `grunt test --suite <suite_name>`

## Maven 

To run the project, the easiest way is to use maven
```
mvn clean compile tomcat7:run -DskipTests -Dspring.profiles.active=vagrant
```

# Tests

# Integration Tests

You have an complete directory with shell scripts to select scenarii : **integration-tests**

or 
```
mvn clean test "-Dtest=*IT"
```

More information [there](https://github.com/Treeptik/CloudUnit/tree/master/integration-tests) about their executions

# E2E Tests

Documentation : work in progress



