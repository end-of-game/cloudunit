#!/bin/bash

CU_HOME=/home/admincu/cloudunit/
WAR=$CU_HOME/cu-manager/target/ROOT.war
DEST_DIR=$CU_HOME/cu-platform/tomcat

if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

rm -rf $DEST_DIR
mkdir -p $DEST_DIR
cp -f $WAR $DEST_DIR
chown -R admincu:admincu $CU_HOME
