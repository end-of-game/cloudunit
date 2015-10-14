FROM ubuntu:12.04

# DEF DE MONGO USER
RUN groupadd -r mongodb && useradd -r -g mongodb mongodb

# MISE A JOUR DISTRIBUTION
RUN apt-get update

RUN apt-get install -y vim openssh-server cron
RUN mkdir /var/run/sshd
RUN mkdir -p /cloudunit/backup

LABEL upgrade="commit"

ADD scripts /cloudunit/scripts

VOLUME /cloudunit/database

# SSH
EXPOSE 22

