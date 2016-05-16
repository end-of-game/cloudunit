#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/jboss-base images/servers/binaries/jboss-base
docker build --rm --no-cache -t cloudunit/jboss-8 images/servers/binaries/jboss-8
docker build --rm --no-cache -t cloudunit/jboss-appconf8 images/servers/appconf/jboss-appconf8

