#!/bin/bash

set -x

CU_USER=$1
CU_PASSWORD=$2

FILETODEPLOY=`ls $CU_TMP`

mv $CU_TMP/$FILETODEPLOY $CU_SOFTWARE/

kill pidof `java`

$JAVA_HOME/bin/java -jar $CU_SOFTWARE/deploy/$FILETODEPLOY > $CU_LOGS/system.out &

