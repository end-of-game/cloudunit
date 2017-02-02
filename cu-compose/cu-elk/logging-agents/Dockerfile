FROM ubuntu:16.04
MAINTAINER s.musso@treeptik.fr

ENV FILEBEAT_VERSION 5.1.2

## Get Filebeat binaries
RUN mkdir -p /opt/cloudunit/logging-agents \
    && apt update && apt -y install wget curl \
    && cd /opt/cloudunit/logging-agents \
    && wget https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-$FILEBEAT_VERSION-linux-x86_64.tar.gz \
    && tar zxf filebeat-$FILEBEAT_VERSION-linux-x86_64.tar.gz \
    && mv filebeat-$FILEBEAT_VERSION-linux-x86_64 filebeat \
    && rm filebeat-$FILEBEAT_VERSION-linux-x86_64.tar.gz

RUN useradd filebeat \
    && chown -R filebeat:filebeat /opt/cloudunit/logging-agents \
    && apt-get clean \
    && apt-get autoremove

COPY filebeat-conf /opt/cloudunit/logging-agents/filebeat/conf.d
WORKDIR /opt/cloudunit/logging-agents/filebeat
VOLUME /opt/cloudunit/logging-agents

LABEL origin cloudunit-logging
LABEL application-type logging
LABEL application-version 0.1
