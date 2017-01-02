# CloudUnit server installation

You are reading the right guide if you want to set up a CloudUnit server, in order to use it frequently. 
We hope to provide a simple installation following KISS principles.

## Requirements

A virtual or baremetal server with
* at least 8 GB RAM (32 GB or even 64 GB will be better!)
* Ubuntu 14.04 LTS with a 4.x kernel with AUFS support (see FAQ if needed)

> No mysql server must be installed because one will be provided by CloudUnit. 
> If one is already installed on the server, it will create a conflict on port 3306.
> You must remove it before installing CloudUnit. 
> Backup your data if necessary.

## Installation

### As `root` 

Run the command below as `root` to create `admincu` user and install Docker.

```
curl -sSL https://raw.githubusercontent.com/Treeptik/cloudunit/dev/cu-production/get.cloudunit.sh | sh
```

If all prerequisites are met, you can start installation procedure, you can add argument to the script in order to install different version of cloudunit, by default dev one will be installed :

```
./bootstrap.sh
```

During installation process, some information have to be set mannually like domain name to access to your server. For example : `cu01.cloudunit.io` or `cloudunit.local` and the password for `admincu` user.

All module images (tomcat, mysql, postgresql ....) will be downloaded automatically

## Add your own certificate

Once done execute these commands to copy you certificates into the traefik and restart the container :

```
docker cp your-public-key.pem cu-traefik:/certs/traefik.crt 
docker cp your-private-key.pem cu-traefik:/certs/traefik.key
docker restart cu-traefik
```

# FAQ

```

## How to install a 4.x kernel with AUFS support

```
apt-get install linux-image-4.4.0-42-generic
apt-get install -y linux-image-extra-$(uname -r)
```

## How to restart the production environment without reseting data

```
~/cloudunit/cu-compose/start-with-elk.sh
```
You have many start-* files for different scenarii.

## How to reset the production environment 

```
~/cloudunit/cu-compose/reset-prod.sh
~/cloudunit/cu-compose/start-with-elk.sh
```

## How to change the MySQL password

You have to change the MySQL root password (`changeit` by default)
To do so, you have to change the value in the following files
* /home/admincu/.cloudunit/configuration.properties
* /etc/profile

## How to change the SSL Certificates

In order to customize your Cloudunit installation with your own domain name and SSL certificates,
please follow these instructions.

### NGINX config files

NGINX is the entrypoint of the Cloudunit PaaS frontend and is provided as a Docker container.

SSL certificates directory location:

```
~/cloudunit/cu-compose/nginx/DOMAIN_NAME/
```

NGINX global configuration for domain wildcard:

```
~/cloudunit/cu-compose/nginx/nginx.conf
```

NGINX domain configuration for apps (gitlab, jenkins, admin...):
Please rename the following file with your domain name and customize it.

```
~/cloudunit/cu-compose/nginx/sites-enabled/cloudunit.conf
```

### SSL Certificates

The number of certificates to list per Nginx server name depends on your SSL Provider.
Extensions also could be differ from a provider to another.

As an example Globalsign gives, in addition, an intermediate certificate. Some others aggregate and encrypt certificates in PKCS / P7B format. In this case, you have to split the file in multiple standard certificates.

