#!/bin/bash

set -x

export WAR_NAME=$1
export CU_USER=$2
export WAR_PATH=/cloudunit/tmp

echo "#################################"
echo "# CLOUDUNIT SERVER DEPLOY BEGIN #"
echo "#################################"

source /etc/environment

# Delete the current app
rm $JBOSS_HOME/standalone/deployments/*

FILE=$WAR_PATH/$WAR_NAME
# Move the app in deployment
if [[ $WAR_NAME == *.war ]]; then
	mv $WAR_PATH/$WAR_NAME $WAR_PATH/ROOT.war
	FILE=$WAR_PATH/ROOT.war
fi

chown -R $CU_USER:$CU_USER /cloudunit
echo $JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="deploy $FILE"
$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="deploy $FILE"

sleep 2

echo "###############################"
echo "# CLOUDUNIT SERVER DEPLOY END #"
echo "###############################"




