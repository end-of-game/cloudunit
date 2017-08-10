#!/bin/bash

CU_USER=$2
CU_PASSWORD=$3

if [[ ! -z "$CU_USER" ]] && [[ ! -z "$CU_PASSWORD" ]]
then
    PATTERN_USER="s/CU_USER/$CU_USER/g"
    PATTERN_PASSWD="s/CU_PASSWORD/$CU_PASSWORD/g"
    sed -i $PATTERN_USER /opt/cloudunit/tomcat/conf/tomcat-users.xml
    sed -i $PATTERN_PASSWD /opt/cloudunit/tomcat/conf/tomcat-users.xml
fi

if [ -n "$http_proxy" ]; then
  JAVA_OPTS=$JAVA_OPTS" -Dhttp.proxyHost="$(echo $http_proxy | cut -d'/' -f 3 | cut -d':' -f 1)
  JAVA_OPTS=$JAVA_OPTS" -Dhttp.proxyPort="$(echo $http_proxy | cut -d':' -f 3)
fi
if [ -n "$https_proxy" ]; then
  JAVA_OPTS=$JAVA_OPTS" -Dhttps.proxyHost="$(echo $https_proxy | cut -d'/' -f 3 | cut -d':' -f 1)
  JAVA_OPTS=$JAVA_OPTS" -Dhttps.proxyPort="$(echo $https_proxy | cut -d':' -f 3)
fi
if [ -n "$ftp_proxy" ]; then
  JAVA_OPTS=$JAVA_OPTS" -Dftp.proxyHost="$(echo $ftp_proxy | cut -d'/' -f 3 | cut -d':' -f 1)
  JAVA_OPTS=$JAVA_OPTS" -Dftp.proxyPort="$(echo $ftp_proxy | cut -d':' -f 3)
fi
if [ -n "$no_proxy" ]; then
  JAVA_OPTS=$JAVA_OPTS" -Dhttp.nonProxyHost="$no_proxy
  JAVA_OPTS=$JAVA_OPTS" -Dftp.nonProxyHost="$no_proxy
fi

if [[ $1 == "run" ]]; then
  exec catalina.sh "run"
fi

exec "$@"
