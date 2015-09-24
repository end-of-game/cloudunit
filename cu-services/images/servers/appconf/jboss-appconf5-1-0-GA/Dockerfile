# Base Dockerfile for all TOMCAT IMAGES
FROM cloudunit/base-12.04

# AJOUT DE LA VERSION DE TOMCAT
RUN wget https://github.com/Treeptik/cloudunit/releases/download/0.9/jboss-appconf5.1.0.GA.tar.gz -O /tmp/jboss-appconf5.1.0.GA.tar.gz \
	&& tar -xvf /tmp/jboss-appconf5.1.0.GA.tar.gz -C /cloudunit/appconf && rm /tmp/jboss-appconf5.1.0.GA.tar.gz

LABEL upgrade="commit"

# EXPOSE PORT
EXPOSE 22

VOLUME /cloudunit/appconf/server/default/log

ENTRYPOINT ["/cloudunit/scripts/start-service.sh"]

