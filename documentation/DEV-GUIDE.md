
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

### Pre-requisites

* DNS Local resolver
The ubuntu network mananger provided its own local DNS solutin. To begin, you need to disable it.
So uncomment the following line for this config file : 
```bash
/etc/NetworkManager/NetworkManager.conf
#dns=dnsmasq
```
Unable the dnsmasq network manager at startup:
```bash
sudo update-rc.d -f dnsmasq remove
````
Start again the network manager:
```bash
sudo service network-manager restart
````
