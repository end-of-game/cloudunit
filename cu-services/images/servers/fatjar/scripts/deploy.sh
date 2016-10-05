#!/bin/bash

set -x

CU_USER=$1
CU_PASSWORD=$2
CU_FILE=$3
CU_CONTEXT_PATH=$4

mv -f $CU_TMP/$CU_FILE /opt/cloudunit/fatjar/boot.jar

