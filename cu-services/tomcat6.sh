#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/base-14.04 images/base-14.04
docker build --rm --no-cache -t cloudunit/tomcat-6 images/servers/tomcat-6
