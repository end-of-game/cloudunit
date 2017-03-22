#!/bin/bash

set -x

CU_USER=$1
CU_PASSWORD=$2
CU_FILE=$3
CU_CONTEXT_PATH=$4

mv /opt/cloudunit/tmp/$CU_FILE /opt/cloudunit/verticles/verticle.rb

