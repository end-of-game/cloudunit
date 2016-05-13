#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/base-12.04 images/base-12.04
docker build --rm --no-cache -t cloudunit/tomcat-appconf8 images/servers/appconf/tomcat-appconf8
docker build --rm --no-cache -t cloudunit/tomcat-8 images/servers/binaries/tomcat-8
