#!/bin/bash

set -x

source /etc/environment

env

echo $USER

$JAVA_HOME/bin/java -jar /cloudunit/binaries/$DEPLOYED_JAR > /cloudunit/appconf/logs/system.out &

echo "JVM is started"



