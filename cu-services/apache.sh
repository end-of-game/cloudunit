#!/usr/bin/env bash

#docker build --rm --no-cache -t cloudunit/apache-2-2 images/servers/appconf/apache-2-2
docker build --rm --no-cache -t cloudunit/base-12.04 images/base-12.04
docker build --rm --no-cache -t cloudunit/apache-2-2 images/servers/appconf/apache-2-2
