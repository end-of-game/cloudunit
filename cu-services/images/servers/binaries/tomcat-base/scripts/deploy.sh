#!/bin/bash

set -x

export TOMCAT_HOME=/cloudunit/appconf
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
rm -rf $TOMCAT_HOME/webapps/ROOT
rm -rf $TOMCAT_HOME/webapps/${WAR_NAME%.*ar}
rm -rf $TOMCAT_HOME/work/Catalina/localhost/_

#move the war in webapps
mv $WAR_PATH/$WAR_NAME $TOMCAT_HOME/webapps/ROOT.war

#restart the server
su - $RUNNER -c '/cloudunit/scripts/cu-start.sh'

sleep 2
chown -R $RUNNER:$RUNNER $TOMCAT_HOME/webapps
