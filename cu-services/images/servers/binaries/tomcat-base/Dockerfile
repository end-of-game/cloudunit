# Base Dockerfile for all TOMCAT IMAGES
FROM cloudunit/base-12.04

# AJOUT DES SCRIPTS UTILES AU MANAGER
ADD scripts/ /cloudunit/scripts
RUN chmod -R +x /cloudunit/scripts

# EXPOSTION DES PORTS
EXPOSE 22

