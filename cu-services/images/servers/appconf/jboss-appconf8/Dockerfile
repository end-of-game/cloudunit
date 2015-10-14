# Base Dockerfile for all TOMCAT IMAGES
FROM cloudunit/base-12.04

# AJOUT DE LA VERSION DE TOMCAT
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jboss-appconf8.tar.gz -O /tmp/jboss-appconf8.tar.gz \
	&& tar -xvf /tmp/jboss-appconf8.tar.gz -C /cloudunit/appconf && rm /tmp/jboss-appconf8.tar.gz

RUN mkdir -p /cloudunit/appconf/scripts
ADD scripts/ /cloudunit/appconf/scripts
RUN chmod +x /cloudunit/appconf/scripts/*

LABEL upgrade="commit"

# EXPOSE PORT
EXPOSE 22 

VOLUME /cloudunit/appconf/standalone/log

ENTRYPOINT ["/cloudunit/scripts/start-service.sh"]
