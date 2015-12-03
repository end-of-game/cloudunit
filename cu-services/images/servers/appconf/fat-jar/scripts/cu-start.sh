#!/bin/bash

set -x

source /etc/environment

echo "java -jar $DEPLOYED_JAR" >> /cloudunit/appconf/logs/app.log

echo "JVM is started"

