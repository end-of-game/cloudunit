# Demo Vagrant box

This guide is relevant if you want to try CloudUnit without going through any complicated installation procedure. We provide a Vagrant box with CloudUnit version 1.0.

## Requirements
* Virtualbox version 5.0.4+
* Vagrant plugin ?

## Start the box
You can launch it in 2 commands:
```
$ wget https://github.com/Treeptik/CloudUnit/releases/download/1.0/Vagrantfile
$ vagrant up
```

The default box IP address is 192.168.50.4. If you want it to be reachable on the network, that is to say in bridge mode, change the line `config.vm.network "private_network", ip: "192.168.50.4"` for `config.vm.network "public_network"` in the `Vagrantfile`.

### Local DNS (Linux Debian)

You need to add a local DNS entry pointing to the vagrant IP address. More precisely, any address ending with admin.cloudunit.io shoud point to `192.168.50.4`. On Ubuntu, a simple way to achieve this is to install dnsmasq:
```
$ sudo apt-get install dnsmasq
```
Then edit the file `/etc/dnsmasq.conf` and add the line:
```
address=/.demo.cloudunit.io/192.168.50.4
```
Finally, restart dnsmasq:
```
$ sudo service dnsmasq restart
```

### Local DNS (MacOSX)

You need to add a local DNS entry pointing to the vagrant IP address. More precisely, any address ending with .demo.cloudunit.io shoud point to `192.168.50.4`. On Ubuntu, a simple way to achieve this is to install dnsmasq:
```
# Update your homebrew installation
brew up
# Install dnsmasq
brew install dnsmasq
```
Then edit the file `/usr/local/etc/dnsmasq.conf` and add the line:
```
address=/.demo.cloudunit.io/192.168.50.4
```
Finally, start dnsmasq:
```
$ sudo launchctl start homebrew.mxcl.dnsmasq
```
For more information in this environment, please read this [article](http://passingcuriosity.com/2013/dnsmasq-dev-osx/)


## Enjoy CloudUnit
CloudUnit WebUI is available at the box IP. You can login with the credentials johndoe / abc2015.

URL entrypoint is [https://demo.cloudunit.io](https://demo.cloudunit.io)

You should get an "Insecure connection" warning. This is expected as we don't provide you with our certificates for the cloudunit.io domain!

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/CU-login.png)


## Reset CloudUnit
If you want to restart from the beginning, you can reset the platform.

![](https://github.com/Treeptik/CloudUnit-images/blob/master/warning2.png)

**All your data will be lost!**

In the repository where you have downloaded the `Vagrantfile`, run: `vagrant ssh` to ssh into the box. Finally reset everything:
```
$ vagrant ssh -c "cloudunit reset"
```

