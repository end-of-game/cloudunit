#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/mysql-5-5 images/modules/mysql-5-5
docker build --rm --no-cache -t cloudunit/mysql-5-6 images/modules/mysql-5-6
docker build --rm --no-cache -t cloudunit/mysql-5-7 images/modules/mysql-5-7
