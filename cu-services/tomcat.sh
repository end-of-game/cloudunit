#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/base-14.04 images/base-14.04
docker build --rm --no-cache -t cloudunit/tomcat-6 images/servers/tomcat-6
docker build --rm --no-cache -t cloudunit/tomcat-7 images/servers/tomcat-7
docker build --rm --no-cache -t cloudunit/tomcat-8 images/servers/tomcat-8
docker build --rm --no-cache -t cloudunit/tomcat-85 images/servers/tomcat-85
docker build --rm --no-cache -t cloudunit/tomcat-9 images/servers/tomcat-9
