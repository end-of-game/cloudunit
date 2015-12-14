# Base Dockerfile for all TOMCAT IMAGES
FROM cloudunit/base-12.04

# ADD SCRIPTS FOR CU MANAGER
ADD scripts/ /cloudunit/scripts
RUN chmod -R +x /cloudunit/scripts

#ADD 
ADD binaries/ /cloudunit/binaries

LABEL upgrade="commit"

# EXPOSE PORT
EXPOSE 22

VOLUME /cloudunit/appconf/logs

ENTRYPOINT ["/cloudunit/scripts/start-service.sh"]

