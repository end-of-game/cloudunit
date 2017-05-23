#!/bin/bash

set -x

CU_USER=$TOMCAT_USER
CU_PASSWORD=$TOMCAT_PASSWORD
CU_FILE=$1
CU_CONTEXT_PATH=$2

curl "http://$CU_USER:$CU_PASSWORD@localhost:8080/manager/undeploy?path=$CU_CONTEXT_PATH"
curl "http://$CU_USER:$CU_PASSWORD@localhost:8080/manager/deploy?war=file:$CU_TMP/$CU_FILE&path=$CU_CONTEXT_PATH"
