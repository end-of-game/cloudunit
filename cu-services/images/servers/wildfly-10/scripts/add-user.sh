#!/usr/bin/env bash

set -x

CU_USER=$1
CU_PASSWORD=$2

$JBOSS_HOME/bin/add-user.sh --silent=true $CU_USER $CU_PASSWORD

set +x

