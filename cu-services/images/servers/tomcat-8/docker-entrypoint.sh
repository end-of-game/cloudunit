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

# if $APPLICATIVE_MONITORING doesn't exist or is equals to 1
if [ -z "$APPLICATIVE_MONITORING" ] || [ "$APPLICATIVE_MONITORING" -eq 1 ]; then
	/opt/cloudunit/monitoring-agents/metricbeat/metricbeat -c /opt/cloudunit/monitoring-agents/metricbeat/conf.d/nginx.yml&
fi

# if $JMX_MONITORING doesn't exist or is equals to 1
if [ -z "$JMX_MONITORING" ] || [ "$JMX_MONITORING" -eq 1 ]; then
    JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/cloudunit/tomcat/lib/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar=/opt/cloudunit/conf/jmxtrans-agent.xml"
fi

# if $JMX_MONITORING doesn't exist or is equals to 1
if [ -z "$MELODY_MONITORING" ] || [ "$MELODY_MONITORING" -eq 1 ]; then
    mv /opt/cloudunit/tomcat/conf/tomcat-users.xml.melody /opt/cloudunit/tomcat/conf/tomcat-users.xml
    mv /opt/cloudunit/tomcat/conf/logging.properties.melody /opt/cloudunit/tomcat/conf/logging.properties
    mv /opt/cloudunit/tomcat/conf/web.xml.melody /opt/cloudunit/tomcat/conf/web.xml
fi

if [[ $1 == "run" ]]; then
  exec catalina.sh "run"
fi

exec "$@"
