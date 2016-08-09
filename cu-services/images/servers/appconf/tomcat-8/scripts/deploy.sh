#!/bin/bash

set -x

export ENV_FILE="/opt/cloudunit/.profile"
source $ENV_FILE

CU_USER=$1
CU_PASSWORD=$2

FILETODEPLOY=`ls $CU_TMP`
mv $CU_TMP/$FILETODEPLOY $CU_TMP/ROOT.war

curl 'http://'$CU_USER:$CU_PASSWORD'@localhost:8080/manager/text/deploy?war=file:'$CU_TMP'/ROOT.war&path=/'

rm -f $CU_TMP/$FILETODEPLOY

