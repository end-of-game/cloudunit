#!/bin/bash

set -x

export CU_HOME=/cloudunit/appconf
export JAR_NAME=$1
# We need it for git
export RUNNER=$2
export JAR_PATH=/cloudunit/tmp

sed -i 's/^DEPLOYED_JAR=.*$/DEPLOYED_JAR='$JAR_NAME'/g' /etc/environment

# stop the server
/cloudunit/scripts/cu-stop.sh

# wait for its downtime
/cloudunit/scripts/waiting-for-shutdown.sh java 30

# The server is down. We clean the logs
# because they are stored into ElasticSearch
rm -rf /cloudunit/appconf/logs/*

#delete the current app
rm -f $CU_HOME/*

#move the war in webapps
mv $JAR_PATH/$JAR_NAME /cloudunit/binaries/$JAR_NAME

#restart the server
echo "su - $RUNNER -c '/cloudunit/scripts/cu-start.sh'"

su - $RUNNER -c '/cloudunit/scripts/cu-start.sh'

