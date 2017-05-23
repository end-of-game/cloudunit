#!/bin/bash

set -x

export CU_USER=$WILDFLY_USER
export CU_PASSWORD=$WILDFLY_PASSWORD
export CU_FILE=$1
## Ignored for the moment
# export CU_CONTEXT_PATH=$2

$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="undeploy $CU_FILE"
$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="deploy $CU_TMP/$CU_FILE"

set +x
