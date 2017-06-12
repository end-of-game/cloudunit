#!/bin/bash

if [[ ! -z "$TOMCAT_USER" ]] && [[ ! -z "$TOMCAT_PASSWORD" ]]
then
    cat > conf/tomcat-users.xml <<EOF
<?xml version='1.0' encoding='utf-8'?>

<tomcat-users
    xmlns="http://tomcat.apache.org/xml"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
    version="1.0">
    <role rolename="manager-gui"/>
    <role rolename="manager-script"/>
    <role rolename="manager-jmx"/>
    <user username="$TOMCAT_USER" password="$TOMCAT_PASSWORD" roles="manager-gui,manager-script,manager-jmx"/>
</tomcat-users>
EOF
fi

# if $JMX_MONITORING doesn't exist or is equals to 0
if [ "$JMX_MONITORING" -eq 0 ]; then
    JAVA_OPTS="$JAVA_OPTS -javaagent:lib/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar=conf/jmxtrans-agent.xml"
fi

exec "$@"

