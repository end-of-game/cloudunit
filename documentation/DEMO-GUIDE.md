# Demo Vagrant box

This guide is relevant if you want to try CloudUnit without going through any complicated installation procedure. We provide a Vagrant box with CloudUnit version 1.0.

## Requirements
* Virtualbox version 5.0.4+
* Vagrant plugin ?

## Start the box
You can launch it in 2 commands:
```
$ vagrant init treeptik/CUdemo
$ vagrant up
```

The default box IP address is 192.168.50.4, but if you want it to be reachable on the network, uncomment the line `config.vm.network "public_network"` in the `Vagrantfile`.

## Enjoy CloudUnit
CloudUnit WebUI is available at the box IP. You can login with the credentials johndoe / abc2015.

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/CU-login.png)

