#!/bin/bash

export ENV_FILE="/opt/cloudunit/.profile"
source $ENV_FILE

export MEMORY_VALUE=$1
export JVM_OPTIONS=$2

#stop tomcat server
$CU_SCRIPTS/cu-stop.sh

#Wait for server shutdown
$CU_SCRIPTS/waiting-for-shutdown.sh java 30

echo "sed -i 's/^CATALINA_OPTS=.*$/CATALINA_OPTS="-Dfile.encoding=UTF-8 -Xms'$MEMORY_VALUE'm -Xmx'$MEMORY_VALUE'm -XX:MaxPermSize=256m '"$JVM_OPTIONS"'"/g' $ENV_FILE" >> /opt/cloudunit/command.txt

sed -i 's/^CATALINA_OPTS=.*$/CATALINA_OPTS="-Dfile.encoding=UTF-8 -Xms'$MEMORY_VALUE'm -Xmx'$MEMORY_VALUE'm -XX:MaxPermSize=256m '"$JVM_OPTIONS"'"/g' $ENV_FILE

source $ENV_FILE

#restart the server
$CU_SCRIPTS/cu-start.sh

