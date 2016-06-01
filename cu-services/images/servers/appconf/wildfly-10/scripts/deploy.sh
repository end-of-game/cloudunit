#!/bin/bash

set -x

export WAR_NAME=$1
export CU_USER=$2
export WAR_PATH=/cloudunit/tmp

echo "#################################"
echo "# CLOUDUNIT SERVER DEPLOY BEGIN #"
echo "#################################"

source /etc/environment

/cloudunit/scripts/cu-stop.sh

# Delete the current app
rm $JBOSS_HOME/standalone/deployments/*

# Move the app in deployment
if [[ $WAR_NAME == *.war ]]; then
	mv $WAR_PATH/$WAR_NAME $JBOSS_HOME/standalone/deployments/ROOT.war
	touch $JBOSS_HOME/standalone/deployments/ROOT.war.dodeploy
fi

if [[ $WAR_NAME == *.ear ]]; then
	mv $WAR_PATH/$WAR_NAME $JBOSS_HOME/standalone/deployments/$WAR_NAME
	touch $JBOSS_HOME/standalone/deployments/$WAR_NAME.dodeploy
fi

chown -R $CU_USER:$CU_USER /cloudunit
/cloudunit/scripts/cu-start.sh

sleep 2

echo "###############################"
echo "# CLOUDUNIT SERVER DEPLOY END #"
echo "###############################"




