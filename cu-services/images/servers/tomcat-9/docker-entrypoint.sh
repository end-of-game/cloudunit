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

if [ -z "$APPLICATIVE_LOGGING" ] || [ "$APPLICATIVE_LOGGING" -eq 1 ]; then
  /opt/cloudunit/logging-agents/filebeat/filebeat -c /opt/cloudunit/logging-agents/filebeat/conf.d/tomcat.yml -path.data /tmp&
fi

# if $JMX_MONITORING doesn't exist or is equals to 1
if [ -z "$JMX_MONITORING" ] || [ "$JMX_MONITORING" -eq 1 ]; then
    JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/cloudunit/tomcat/lib/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar=/opt/cloudunit/conf/jmxtrans-agent.xml"
fi

if [[ $1 == "run" ]]; then
  exec catalina.sh "run"
fi

exec "$@"
