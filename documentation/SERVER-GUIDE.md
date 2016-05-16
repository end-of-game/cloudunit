# CloudUnit server installation

You are reading the wright guide if you want to setup a CloudUnit server, in order to frequently use it. 

## Requirements

* A server with Ubuntu 14.04 LTS
* Git installed and a root account
* Mysql **client** only installed : *apt-get install -y mysql-client*

```No mysql server because it is provided by docker.  If present, you must save your data and remove it. ```

## Installation from another host

```
$ git clone https://github.com/Treeptik/cloudunit.git
$ cd cloudunit/cu-production
$ ./boot.sh
```

