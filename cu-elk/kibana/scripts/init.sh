#!/usr/bin/env bash

CU_USER=$1
CU_PASSWORD=$2

PATTERN_USER="s/CU_USER/$CU_USER/g"
PATTERN_PASSWD="s/CU_PASSWORD/$CU_PASSWORD/g"

sed -i $PATTERN_USER /usr/local/tomcat/conf/tomcat-users.xml
sed -i $PATTERN_PASSWD /usr/local/tomcat/conf/tomcat-users.xml
