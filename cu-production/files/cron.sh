#!/bin/bash

STATUS=`docker inspect -f {{.State.Running}} cuplatform_tomcat_1`
if [ "$STATUS"=="false" ]; then
    cd /home/admincu/cloudunit/cu-compose
    /home/admincu/cloudunit/cu-compose/start-with-elk.sh
    docker-compose kill hipache
    docker-compose rm -f hipache
    docker-compose up -d hipache
fi

