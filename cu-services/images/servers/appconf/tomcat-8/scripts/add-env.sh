#!/bin/bash

set -x

export ENV_FILE=/etc/environment

CU_KEY=$1
CU_VALUE=$2

# REMOVE THE LINE
sed '/CU_KEY/d' $ENV_FILE

# ADD THE LINE
echo "$CU_KEY=$CU_VALUE" >> $ENV_FILE


