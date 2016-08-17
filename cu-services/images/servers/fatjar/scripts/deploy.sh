#!/bin/bash

set -x

CU_USER=$1
CU_PASSWORD=$2

FILETODEPLOY=`ls $CU_TMP`

rm -rf $CU_SOFTWARE/deployments/*

mv $CU_TMP/$FILETODEPLOY $CU_SOFTWARE/deployments

$JAVA_HOME/bin/java -jar $CU_SOFTWARE/deployments/$FILETODEPLOY > $CU_LOGS/system.out &

