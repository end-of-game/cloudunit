#!/bin/bash

export USERNAME=$1
export PASSWORD=$2
export APPNAME=$3
export SCRIPT_PATH=/cloudunit/software/tmp

mysql -u$USERNAME -p$PASSWORD $APPNAME<$SCRIPT_PATH/initData.sql
