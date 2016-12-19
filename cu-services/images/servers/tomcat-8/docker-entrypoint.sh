#!/bin/bash

set -x

CU_USER=$2
CU_PASSWORD=$3

if [[ ! -z "$CU_USER" ]] && [[ ! -z "$CU_PASSWORD" ]]
then
    PATTERN_USER="s/CU_USER/$CU_USER/g"
    PATTERN_PASSWD="s/CU_PASSWORD/$CU_PASSWORD/g"
    sed -i $PATTERN_USER /opt/cloudunit/tomcat/conf/tomcat-users.xml
    sed -i $PATTERN_PASSWD /opt/cloudunit/tomcat/conf/tomcat-users.xml
fi

if [[ $1 == "run" ]]; then
  exec catalina.sh "run"
fi

exec "$@"
