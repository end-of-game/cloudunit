#!/bin/bash

set -x

export CU_USER=$1
export CU_PASSWORD=$2

WAR_NAME=`ls $CU_TMP`
FILE=$CU_TM/$WAR_NAME

# Move the app in deployment
if [[ $WAR_NAME == *.war ]]; then
	mv $WAR_PATH/$WAR_NAME $WAR_PATH/ROOT.war
	FILE=$WAR_PATH/ROOT.war
fi

$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="deploy $FILE"

set +x


