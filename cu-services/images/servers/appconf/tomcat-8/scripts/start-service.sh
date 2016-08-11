#!/bin/bash

set -x

# DEFAULT JVM
export JAVA_HOME=/opt/cloudunit/java/jdk1.8.0_25

# Callback bound to the application stop
pid=0
term_handler() {
  if [ $pid -ne 0 ]; then
    /opt/cloudunit/scripts/cu-stop.sh
	/opt/cloudunit/scripts/waiting-for-shutdown.sh java 30
  fi
  exit 42;
}

trap 'term_handler' SIGTERM

if [ ! -f /opt/cloudunit/init-service-ok ]; then
	rm -rf $CU_SOFTWARE/webapps/ROOT $CU_SOFTWARE/webapps/examples $CU_SOFTWARE/webapps/docs
	touch /opt/cloudunit/init-service-ok
fi

# Lancement de tomcat avec Attente du demarrage de tomcat
/opt/cloudunit/scripts/cu-start.sh

pid=`pidof java`

# wait indefinetely
while true
do
  tail -f /dev/null & wait ${!}
done


