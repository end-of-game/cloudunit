# Installing CloudUnit on a server

You are reading the right guide if you would like to set up a CloudUnit server.
This guide aims to provide install instructions that follow the [KISS principle](https://en.wikipedia.org/wiki/KISS_principle).

## Requirements

A virtual or baremetal server with
* at least 16 GB RAM (but we recommend at least 32 GB)
* one of the following operating systems
  * Ubuntu 14.04 LTS with a 4.x kernel with AUFS support
  * Or Centos 7.x with LVM2 package installed and a lvm vg named "docker" (see FAQ if needed)
* a domain name and a DNS entries on your network or the Internet that point that domain name and all of its sub-domains to the IP of the server (this guide uses *.cloudunit.io)
* _(for proper SSL support)_ an SSL certificate for the server's domain name and all of its sub-domains
* SE Linux disabled on Centos operating system

## Install

Log in to your server as `root`.

Download the install script and check that all prerequisites are present.

```
curl -sSL https://raw.githubusercontent.com/Treeptik/cloudunit/dev/cu-production/get.cloudunit.sh | sh
```

Install and start CloudUnit, installing Docker if it isn't already.

```
./bootstrap.sh
```

During the install, you will be required to answer a few questions, such as whether to download (pull) or
build the components of the CloudUnit platform.

By default, the latest cutting-edge version from branch `dev` will be installed. If you would rather install
a different version, you may pass its tag or branch name as an argument to the installer. Branch `master` always
contains the latest stable release.

```
./bootstrap.sh [branch]
./bootstrap.sh dev
./bootstrap.sh master
```

## Unattended install

As mentioned before, the bootstrap script asks a few questions, mainly in order to customize the platform.
If you would like to do an unattended install, create a shell script named `.env` that sets the environment
variables that will be read. This script must be in the directory where you will execute the bootstrap script.
You can use the following example as inspiration for writing your own `.env` customization script.

```
# Set CloudUnit deployment Environment

CU_DOMAIN=cloudunit.io                       # Domain for all created application ex: myapp.cloudunit.io
CU_MANAGER_DOMAIN=cloudunit.io               # Url within Cloudunit UI will be reachable
CU_GITLAB_DOMAIN=gitlab.cloudunit.io         # Url within Gitlab UI will be reachable
CU_JENKINS_DOMAIN=jenkins.cloudunit.io       # Url within Jenkins UI will be reachable
CU_KIBANA_DOMAIN=kibana.cloudunit.io         # Url within Kibana UI will be reachable
CU_MATTERMOST_DOMAIN=mattermost.cloudunit.io # Url within Lets Chat UI will be reachable
CU_NEXUS_DOMAIN=nexus.cloudunit.io           # Url within Nexus UI will be reachable
CU_SONAR_DOMAIN=sonar.cloudunit.io           # Url within Sonar UI will be reachable
ELASTICSEARCH_URL=elasticsearch              # Url of elasticsearch database default to internal one
MYSQL_ROOT_PASSWORD=changeit                 # Change Mysql Root Password
MYSQL_DATABASE=cloudunit                     # Mysql Database name
HOSTNAME=cloudunit-host                      # Server hostname
```

# FAQ


## How to install a 4.x kernel with AUFS support

```
apt-get install linux-image-4.4.0-42-generic
apt-get install -y linux-image-extra-$(uname -r)

```

## How to install lvm2 package for Centos Distro

```
yum -y update && yum -y install lvm2

```

## How to create a fullspace volume (Digital Ocean)

```
pvcreate /dev/sda
vgcreate docker /dev/sda

lvcreate --wipesignatures y -n thinpool docker -l 95%VG
lvcreate --wipesignatures y -n thinpoolmeta docker -l 1%VG

lvconvert -y \
--zero n \
-c 512K \
--thinpool docker/thinpool \
--poolmetadata docker/thinpoolmeta
```

## How to disable SELinux on CentOS

```
sudo vim /etc/selinux/config

change SELINUX=disabled

reboot
```

## How to restart the production environment without reseting data

```
~/cloudunit/cu-compose/cu-docker-compose.sh with-elk
```

## How to reset the production environment 

```
~/cloudunit/cu-compose/cu-docker-compose.sh reset
```

## How to change the MySQL password

You have to change the MySQL root password (`changeit` by default)
To do so, you have to change the value in the following files
* /home/admincu/.cloudunit/configuration.properties
* /etc/profile

## How to change the SSL Certificates

In order to customize your CloudUnit installation with your own domain name and SSL certificates,
please follow these instructions.

Execute the following commands to copy you certificates into the traefik and restart the container:

```
docker cp /path/to/your-public-key.crt cu-traefik:/certs/traefik.crt 
docker cp /path/to/your-private-key.pem cu-traefik:/certs/traefik.key
docker restart cu-traefik
``` 

## How to configure local domain search and DNS servers

Docker doesn't cascade all of the host machine's network configuration to containers.

Any local DNS servers must be configured explicitly as options in `/etc/default/docker`.
In order for changes to be taken into account, the Docker daemon must be restarted using the shell command `sudo service docker restart`.

The following is an example of `/etc/default/docker` that configures several DNS servers, including Google's servers.

```
DOCKER_OPTS="--dns 192.168.2.249 --dns 8.8.8.8 --dns 8.8.4.4"
```

## How to activate and configure LDAP authentication for the CloudUnit Manager

Two steps are required:
* Set `CU_SECURITY=ldap` in the `.env` file mentioned above.
* Add a configuration file at `~admincu/.cloudunit.properties`, if it doesn't already exist, and set the following properties:
  * `security.ldap.urls`: a list of URLs referencing the primary and secondary LDAP servers to bind to
  * `security.ldap.basedn`: the Base DN to bind to
  * `security.ldap.manager.user` and `security.ldap.manager.password`: username and password for the service account to use to bind to any of the LDAP servers given. The user need only have read access.
  * `security.ldap.user.login-field`: the attribute that will be searched in order to find a username
  * `security.ldap.user.objectclass`: the object class that users must have (defaults to `*`, meaning any class)
  * `security.ldap.group.search-base`: the DN under which all user roles can be found
  * `security.ldap.group.objectclass`: the object class that groups must have (same default as for users)

The following file is an example that works with an Active Directory.

```
security.ldap.urls=ldap://ldap.your-company.com
security.ldap.basedn=dc=your-company,dc=com
security.ldap.manager.user=sa
security.ldap.manager.password=changeit13!!
security.ldap.user.login-field=sAMAccountName
security.ldap.user.objectclass=person
security.ldap.group.search-base=cn=Users
security.ldap.group.objectclass=group
```

