#!/bin/bash

CU_USER=$2
CU_PASSWORD=$3

if [[ ! -z "$CU_USER" ]] && [[ ! -z "$CU_PASSWORD" ]]
then
    /opt/cloudunit/wildfly/bin/add-user.sh --silent=true $CU_USER $CU_PASSWORD
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
  /opt/cloudunit/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
fi

exec "$@"
