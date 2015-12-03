#!/bin/bash

set -x

export CU_HOME=/cloudunit/appconf
export WAR_NAME=$1
# We need it for git
export RUNNER=$2
export WAR_PATH=/cloudunit/tmp

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
mv $WAR_PATH/$WAR_NAME $CU_HOME/ROOT.war

#restart the server
if [ $USER = "root" ];then
	/bin/bash -c "su - $RUNNER -c '/cloudunit/scripts/cu-start.sh'"
fi
if [ $USER = $RUNNER ];then
	/cloudunit/scripts/cu-start.sh
fi

sleep 2
chown -R $RUNNER:$RUNNER $CU_HOME
