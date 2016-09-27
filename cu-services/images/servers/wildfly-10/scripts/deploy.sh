#!/bin/bash

set -x

export CU_USER=$1
export CU_PASSWORD=$2
export CU_FILE=$3
## Ignored for the moment
# export CU_CONTEXT_PATH=$4

$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="undeploy $CU_FILE"
$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="deploy $CU_TMP/$CU_FILE"

set +x
