#!/bin/bash

set -x

export CU_USER=$1
export CU_PASSWORD=$2
export CU_FILE=$3
export CU_CONTEXT_PATH=$4

asadmin deploy --force=true --contextroot $CU_CONTEXT_PATH $CU_FILE

set +x
