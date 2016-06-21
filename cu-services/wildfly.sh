#!/usr/bin/env bash

#docker build --rm --no-cache -t cloudunit/wildfly-10 images/servers/appconf/wildfly-10
docker build --rm -t cloudunit/wildfly-8 images/servers/appconf/wildfly-8
docker build --rm -t cloudunit/wildfly-9 images/servers/appconf/wildfly-9
docker build --rm -t cloudunit/wildfly-10 images/servers/appconf/wildfly-10


