# CloudUnit server installation

You are reading the right guide if you want to set up a CloudUnit server, in order to use it frequently. 

## Requirements

A virtual or baremetal server with
* at least 8 GB RAM (32 GB or even 64 GB will be better!)
* Ubuntu 14.04 LTS with a 4.x kernel with AUFS support (see FAQ if needed)

> No mysql server must be installed because one will be provided by CloudUnit. 
> If one is already installed on the server, it will create a conflict on port 3306.
> You must remove it before installing CloudUnit. Backup your data if necessary.

## Installation

### ROOT 

We hope to provide a simple installation following KISS principles.
So you just need to run this command as **ROOT** to create **admincu** user.
You can change the default branch **dev** by **master** if needed into bootstap.sh

```
curl https://raw.githubusercontent.com/Treeptik/cloudunit/dev/cu-production/bootstrap.sh > bootstrap.sh
sh bootstap.sh dev
```

After installation, you need to set a password for *admincu* user account.
Else recopy your private keys to access it.

## ADMINCU

Open a new session with **admincu** on the server.

```
cd /home/admincu/cloudunit/cu-service && ./build-services.sh all
```

You can check the image if you want with 

```
cd /home/admincu/cloudunit/cu-service && ./check_build_images.sh
```

To finish you have to run the (re)init processus for the platform

```
cd /home/admincu/cloudunit/cu-compose && ./re-init.sh
```

## Configuration

### CloudUnit properties.

The default configuration file is `/home/admincu/.cloudunit/configuration.properties`

The template is :  

```
# ################################################################################ #
#                                                                                  #
#      >>  FILE TO PUT INTO ${USER.HOME}/.cloudunit/configuration.properties       #
#                                                                                  #
# ################################################################################ #

# label for UI
cloudunit.instance.name=PROD

# database password
database.password=changeit

# database password must be the same in cu-platform/docker-compose.yml
# database.hostname=cuplatform_mysql_1.mysql.cloud.unit
# database.port=3306
# database.schema=cloudunit
# database.user=root

#mail server configuration :
#admin.email=g.martial@treeptik.fr
#email.active=true
#email.host=smtp.gmail.com
#email.port=587
#email.protocol=smtp
#email.username=support.cloudunit@treeptik.fr
#email.password=
```

Add into ```/etc/environment``` *your* URLs

```
CU_MANAGER_URL=https://manager-demo.cloudunit.io
CU_GITLAB_URL=https://gitlab-demo.cloudunit.io
CU_JENKINS_URL=https://jenkins-demo.cloudunit.io
CU_KIBANA_URL=https://kibana-demo.cloudunit.io
CU_SUB_DOMAIN=.demo
```

# FAQ

## How to install a 4.x kernel with AUFS support

```
apt-get install linux-image-4.4.0-42-generic
apt-get install -y linux-image-extra-$(uname -r)
```

## How to restart Environment Production without reseting data

```
/home/admincu/cloudunit/cu-compose/restart.sh
```

## How to reset Environment Production

```
/home/admincu/cloudunit/cu-compose/reset.sh
```

## How to change Mysql Password 

You have to change MYSQL root password (*changeit* by default)
To do it, you have to change the 
* /home/admincu/.cloudunit/configuration.properties
* /etc/profile

## How to change SSL Certificates

In order to customize your Cloudunit installation with your own domain name and SSL certificates,
please follow these instructions.

### NGINX config files

NGINX is the entrypoint of the Cloudunit PAAS frontend and is provided as docker conatainer.

SSL certificates directory location:

```
/home/admincu/cloudunit/cu-compose/nginx/DOMAIN_NAME/
```

NGINX global configuration for domain wildcard:

```
/home/admincu/cloudunit/cu-compose/nginx/nginx.conf
```

NGINX domain configuration for apps (gitlab, jenkins, admin...):
Please rename the following file with your domain name and customize it.

```
/home/admincu/cloudunit/cu-compose/nginx/sites-enabled/cloudunit.conf
```

### SSL Certificates

The number of certificates to list per Nginx server name depends on your SSL Provider.
Extensions also could be differ from a provider to another.

As an example Globalsign gives, in addition, an intermediate certificate. Some others aggregate and encrypt certificates in PKCS / P7B format. In this case, you have to split the file in multiple standard certificates.

