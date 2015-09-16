# Demo Vagrant box

A Vagrant box with a fresh version of CloudUnit is available.

## Requirements
Virtualbox version 5.0.4+
Vagrant plugin ?

## Start the box
You can launch it in 2 commands:
```
$ vagrant init treeptik/CUdemo
$ vagrant up
```

The default box IP address is 192.168.50.4, but if you want it to be reachable on the network, uncomment the line `config.vm.network "public_network"` in the `Vagrantfile`.

## Enjoy CloudUnit
CloudUnit WebUI is available at the box IP. You can login with the credentials johndoe / abc2015.

![](https://github.com/Treeptik/CloudUnit/releases/download/0.9/CU-login.png)
