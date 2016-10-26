#!/bin/bash

# DO NOTHING

# Ancient Greek philosophy about fatjar is to do nothing.
# Just replace the jar and restart.
# We cannot update the jar with application server without playing with signal traps...
# and we don't want.

set -x

CU_USER=$1
CU_PASSWORD=$2
CU_FILE=$3
CU_CONTEXT_PATH=$4

mv /opt/cloudunit/tmp/$CU_FILE /opt/cloudunit/tmp/boot.jar

