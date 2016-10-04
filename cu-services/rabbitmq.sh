#!/usr/bin/env bash

#docker build --rm --no-cache -t cloudunit/base-jessie images/base-jessie
docker build --rm -t cloudunit/rabbitmq-3.6.5-1 images/modules/rabbitmq-3.6.5-1
