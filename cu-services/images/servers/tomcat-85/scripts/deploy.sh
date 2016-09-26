#!/bin/bash

set -x

CU_USER=$1
CU_PASSWORD=$2
CU_FILE=$3
CU_CONTEXT_PATH=$4

curl "http://$CU_USER:$CU_PASSWORD@localhost:8080/manager/undeploy?path=$CU_CONTEXT_PATH"
curl "http://$CU_USER:$CU_PASSWORD@localhost:8080/manager/deploy?war=file:$CU_TMP/$CU_FILE&path=$CU_CONTEXT_PATH"
