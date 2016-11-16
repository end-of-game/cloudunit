#!/bin/bash

STATUS=`docker inspect -f {{.State.Running}} cuplatform_tomcat_1`
if [ "$STATUS"=="false" ]; then
 cd /home/admincu/cloudunit/cu-compose && /home/admincu/cloudunit/cu-compose/start-with-elk.sh
fi
