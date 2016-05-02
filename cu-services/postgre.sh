#!/usr/bin/env bash

docker build --rm --no-cache -t cloudunit/postgresql-9-3 images/modules/postgresql-9-3
docker build --rm --no-cache -t cloudunit/postgresql-9-4 images/modules/postgresql-9-4
docker build --rm --no-cache -t cloudunit/postgresql-9-5 images/modules/postgresql-9-5
#docker build --rm --no-cache -t cloudunit/postgis-2-2 images/modules/postgis-2-2

