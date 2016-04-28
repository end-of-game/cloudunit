FROM ubuntu:12.04

### TEMPORAIRE - UTILE POUR LE DEVELOPPEMENT

# MISE A JOUR DISTRIBUTION
RUN apt-get update

RUN apt-get install -y vim openssh-server cron
RUN mkdir /var/run/sshd
RUN mkdir -p /cloudunit/backup

LABEL origin cloudunit

ADD scripts /cloudunit/scripts

VOLUME /cloudunit/database

# SSH
EXPOSE 22

