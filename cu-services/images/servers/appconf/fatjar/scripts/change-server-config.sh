#!/bin/bash

export MEMORY_VALUE=$1
export JVM_OPTIONS=$2

#stop tomcat server
su - $CU_USER -c "/cloudunit/scripts/cu-stop.sh"

#Wait for server shutdown
su - $CU_USER -c "/cloudunit/scripts/waiting-for-shutdown.sh java 30"

# The server is down. We clean the logs
# because they are stored into ElasticSearch
rm -rf /cloudunit/appconf/logs/*

sed -i 's/^JAVA_OPTS=.*$/JAVA_OPTS="-Dfile.encoding=UTF-8 -Xms'$MEMORY_VALUE'm -Xmx'$MEMORY_VALUE'm -XX:MaxPermSize=256m '"$JVM_OPTIONS"'"/g' /etc/environment
source /etc/environment

#restart the server
su - $CU_USER -c "/cloudunit/scripts/cu-start.sh"

