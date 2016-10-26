# Base Dockerfile for all IMAGES
FROM ubuntu:12.04

USER root

# Install default software
RUN apt-get update && apt-get install -y vim \
                                         curl \
                                         wget \
                                         unzip \
                                         netcat

# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/backup &&  \
    mkdir -p /opt/cloudunit/hooks/ && \
    mkdir -p /opt/cloudunit/java && \
    mkdir -p /opt/cloudunit/shared && \
    mkdir -p /opt/cloudunit/scripts && \
    mkdir -p /opt/cloudunit/tmp

WORKDIR /opt/cloudunit

# Environment variables
ENV CU_BACKUP /opt/cloudunit/backup
ENV CU_HOOKS /opt/cloudunit/hooks
ENV CU_JAVA /opt/cloudunit/java
ENV CU_SCRIPTS /opt/cloudunit/scripts
ENV CU_SHARED /opt/cloudunit/shared
ENV CU_TMP /opt/cloudunit/tmp

COPY hooks /opt/cloudunit/hooks
RUN chmod a+x /opt/cloudunit/hooks/*

# needed for shell script filter and deletion
LABEL origin cloudunit
