#!/bin/bash

# DO NOTHING

# Ancient Greek philosophy about fatjar is to do nothing.
# Just replace the jar and restart.
# We cannot update the jar with application server without playing with signal traps...
# and we don't want.

CU_FILE=$1

mv /opt/cloudunit/tmp/$CU_FILE /opt/cloudunit/tmp/boot.jar

