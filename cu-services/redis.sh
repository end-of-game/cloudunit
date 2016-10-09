#!/usr/bin/env bash

docker build --rm -t cloudunit/redis-2-8 images/modules/redis-2-8
docker build --rm -t cloudunit/redis-3-0 images/modules/redis-3-0

