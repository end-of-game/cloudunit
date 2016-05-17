# CloudUnit server installation

You are reading the wright guide if you want to setup a CloudUnit server, in order to frequently use it. 

## Requirements

* A server with Ubuntu 14.04 LTS with a 3.13 or 3.19 kernel. Ok for 4.x
* Git installed and a root account

```No mysql server because it is provided by docker.  If present, you must save your data and remove it. ```

## Installation

We hope to provide a simple installation following KISS principles.
So you just need to run this command as root :

```
curl -sL https://raw.githubusercontent.com/Treeptik/cloudunit/dev/cu-production/boot.sh | bash
```

## Configuration

# CloudUnit properties.

The default configuration files is `/home/admincu/.docker/application.properties`

# Database password 

You have to change MYSQL root password (*changeit* by default)
To do it, you have to change the 
* /home/admincu/.docker/application.properties
* /etc/profile

Run `/home/admincu/cloudunit/cu-platform/reset.sh -y`

## Certificats

By default, we cannot know your domain name. So we provide default certificats for HTTPS but without valid CA.
You have to replace those provided by yours.

NGINX is provided as docker container. The certificats can be modified on filesystem through a volume.

## Security

Work in progress
