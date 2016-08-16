#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/base-14.04 images/base-14.04

docker build --rm -t cloudunit/wildfly-8 images/servers/wildfly-8
docker build --rm -t cloudunit/wildfly-9 images/servers/wildfly-9
docker build --rm -t cloudunit/wildfly-10 images/servers/wildfly-10


