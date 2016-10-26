# Base Dockerfile for all SERVER IMAGES
FROM cloudunit/base-14.04

# INSTALLATION JAVA ORACLE EN VERSION 7 et 8
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jdk-7u55-linux-x64.tar.gz -O /tmp/jdk-7u55-linux-x64.tar.gz \
	&& tar -xvf /tmp/jdk-7u55-linux-x64.tar.gz -C /opt/cloudunit/java \
	&& wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jdk-8u25-linux-x64.tar.gz -O /tmp/jdk-8u25-linux-x64.tar.gz \
	&& tar -xvf /tmp/jdk-8u25-linux-x64.tar.gz -C /opt/cloudunit/java && rm /tmp/jdk-*

VOLUME /opt/cloudunit/java
