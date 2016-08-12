#!/bin/bash

set -x

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

# Lancement de tomcat avec Attente du demarrage de tomcat
/opt/cloudunit/scripts/cu-start.sh

pid=`pidof java`

# wait indefinetely
while true
do
  tail -f /dev/null & wait ${!}
done


