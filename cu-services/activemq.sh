#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/base-14.04 images/base-14.04
docker build --rm --no-cache -t cloudunit/activemq-5.13.2 images/modules/activemq-5.13.2
