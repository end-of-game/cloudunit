# CloudUnit server installation

You are reading the wright guide if you want to setup a CloudUnit server, in order to frequently use it. 

## Requirements

* A virtual or baremetal server with at least 8 Go RAM. 32 or 64 will be better !
* A server with Ubuntu 14.04 LTS with a 3.13 or 3.19 kernel. Ok for 4.x
* Git installed and a root account

```No mysql server because it is provided by docker.  If present, you must save your data and remove it. ```

## Installation

We hope to provide a simple installation following KISS principles.
So you just need to run this command as *ROOT* :

```
curl -sL https://raw.githubusercontent.com/Treeptik/cloudunit/dev/cu-production/boot.sh | bash
```

After installation, you need to set a password for *admincu* user account. 

## Configuration

### CloudUnit properties.

The default configuration files is `/home/admincu/.docker/application.properties`
The template is 

```
# cloudunit.max.apps=100

# mail.apiKey=
# mail.emailFrom=
# mail.secretKey=
# mail.smtpHost=smtp.gmail.com
# mail.socketFactoryPort=587
# mail.smtpPort=587

cloudunit.instance.name=PROD

# database password must be the same in /etc/environment
# database.hostname=cuplatform_mysql_1.mysql.cloud.unit
# database.port=3306
# database.schema=cloudunit
# database.user=root
# database.password=changeit

```

### Database password 

You have to change MYSQL root password (*changeit* by default)
To do it, you have to change the 
* /home/admincu/.docker/application.properties
* /etc/profile

Run `/home/admincu/cloudunit/cu-platform/reset.sh -y`

## Certificats

By default, we cannot know your domain name. 
So we provide default certificats for HTTPS but without valid CA.

NGINX is provided as docker container. The certificats can be modified on filesystem through a volume.
You have to replace the certificats into `/home/admincu/cloudunit/cu-production/nginx`

# FAQ

## How to reset Environment Production

```
/home/admincu/cloudunit/cu-production/reset-all.sh -y
```



