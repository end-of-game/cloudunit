# Base Dockerfile for all IMAGES
FROM ubuntu:12.04

# MISE A JOUR DISTRIBUTION ET INSTALLATION OPEN-SSH
RUN apt-get update && apt-get install -y vim openssh-server curl expect netcat

# CREATION DES REPERTOIRES POUR LES ARCHIVES DES PROGRAMMES

RUN mkdir -p /cloudunit/java
RUN mkdir -p /cloudunit/scripts
RUN mkdir -p /cloudunit/binaries
RUN mkdir -p /cloudunit/tmp
RUN mkdir -p /cloudunit/tools
RUN mkdir -p /cloudunit/appconf
RUN mkdir -p /cloudunit/appconf/logs
RUN mkdir -p /cloudunit/software
RUN mkdir -p /cloudunit/appconf/hooks/
RUN mkdir -p /cloudunit/backup
RUN mkdir -p /var/run/sshd

# VARIABLES ENVIRONNEMENTS

ENV CU_LOGS /cloudunit/appconf/logs
ENV CU_JAVA /cloudunit/java
ENV CU_SCRIPTS /cloudunit/scripts
ENV CU_USER_HOME /cloudunit/home
ENV CU_HOOKS /cloudunit/appconf/hooks

RUN echo -n "CU_JAVA=$CU_JAVA\nCU_SCRIPTS=$CU_SCRIPTS\nCU_USER_HOME=$CU_USER_HOME\nCU_LOGS=$CU_LOGS\nCU_HOOKS=$CU_HOOKS\n" >> /etc/environment

COPY hooks /cloudunit/appconf/hooks
RUN chmod a+x /cloudunit/appconf/hooks/*

LABEL origin cloudunit

# AGENT Cloud Unit pour mettre à jour le status en fin de démarrage (ssh)
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/cloudunitAgent-1.0-SNAPSHOT.jar -O /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar
