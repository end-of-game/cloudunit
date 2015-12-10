#!/bin/bash

set -x

source /etc/environment

env

echo $USER

$JAVA_HOME/bin/java -jar $CU_HOME/$DEPLOYED_JAR > /cloudunit/appconf/log/app.log 2>&1

echo "JVM is started"



