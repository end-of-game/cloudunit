#!/bin/bash

STATUS=`docker inspect -f {{.State.Running}} cu-tomcat`
if [ "$STATUS" == "false" ]; then
    cd /home/admincu/cloudunit/cu-compose
    /home/admincu/cloudunit/cu-compose/cu-docker-compose.sh with-elk
fi

