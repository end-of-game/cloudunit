#!/bin/bash

set -x

export ENV_FILE=/etc/environment

echo -n "CU_DATABASE_USER_$4=$1\n" >> $ENV_FILE
echo -n "CU_DATABASE_PASSWORD_$4=$2\n" >> $ENV_FILE
echo -n "CU_DATABASE_DNS_$4=$3\n" >> $ENV_FILE
