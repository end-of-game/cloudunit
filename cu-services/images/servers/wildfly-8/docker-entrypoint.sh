#!/bin/bash

CU_USER=$2
CU_PASSWORD=$3

if [[ ! -z "$CU_USER" ]] && [[ ! -z "$CU_PASSWORD" ]]
then
    /opt/cloudunit/wildfly/bin/add-user.sh --silent=true $CU_USER $CU_PASSWORD
fi

if [ -z "$APPLICATIVE_LOGGING" ] || [ "$APPLICATIVE_LOGGING" -eq 1 ]; then
  /opt/cloudunit/logging-agents/filebeat/filebeat -c /opt/cloudunit/logging-agents/filebeat/conf.d/wildfly.yml -path.data /tmp&
fi

if [[ $1 == "run" ]]; then
  /opt/cloudunit/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0
fi

exec "$@"
