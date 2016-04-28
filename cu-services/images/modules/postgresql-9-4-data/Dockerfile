FROM ubuntu:12.04

### TEMPORAIRE - UTILE POUR LE DEVELOPPEMENT

RUN apt-get update && apt-get install -y openssh-server
RUN mkdir /var/run/sshd
RUN mkdir -p /cloudunit/backup

RUN mkdir -p /cloudunit/backup
ADD scripts /cloudunit/scripts

LABEL origin cloudunit

VOLUME /cloudunit/database

# SSH
EXPOSE 22




