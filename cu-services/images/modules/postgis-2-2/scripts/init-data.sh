#!/bin/bash

export USERNAME=$1
export APPNAME=$3
export SCRIPT_PATH=/cloudunit/software/tmp

psql -U $USERNAME -d $APPNAME -f $SCRIPT_PATH/initData.sql
