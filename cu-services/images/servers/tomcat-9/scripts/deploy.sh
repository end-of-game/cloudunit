#!/bin/bash

set -x

CU_USER=$1
CU_PASSWORD=$2

curl 'http://'$CU_USER:$CU_PASSWORD'@localhost:8080/manager/text/undeploy?path=/'
curl 'http://'$CU_USER:$CU_PASSWORD'@localhost:8080/manager/text/deploy?war=file:'$CU_TMP'/ROOT.war&path=/'

rm -f $CU_TMP/ROOT.war

