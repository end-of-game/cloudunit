#!/usr/bin/env bash

cd $(dirname $0)/selenium
docker-compose down
docker-compose up -d
