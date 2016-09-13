#!/bin/bash

if [ ! -f ${CATALINA_HOME}/scripts/.tomcat_admin_created ]; then
	${CATALINA_HOME}/scripts/create_admin_user.sh
fi

export JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=production"
exec catalina.sh run