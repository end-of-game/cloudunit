#!/bin/bash

set -x

source /etc/environment

echo "java -jar $DEPLOYED_JAR" >> /cloudunit/appconf/log/app.log

echo "JVM is started"

