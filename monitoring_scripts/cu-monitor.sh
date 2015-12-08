#!/bin/bash

LOCK_CM=/tmp/cu-monitor.lock

if [ -e "$LOCK_CM" ]; then
	echo "cloudunitmonitor est déjà en train d'être exécuté"
	exit 1
else
	touch $LOCK_CM

	echo -n -e "\nExécution de cloudunitmonitor.\n"
	if [ ! -f /home/admincu/cloudunit/monitoring_scripts/cloudunitmonitor.jar ]; then
		wget https://github.com/Treeptik/cloudunit/releases/download/1.0/cloudunitmonitor.jar -O /home/admincu/cloudunit/monitoring_scripts/cloudunitmonitor.jar
	fi
	java -Xms128m -Xmx128m -jar /home/admincu/cloudunit/monitoring_scripts/cloudunitmonitor.jar $(docker inspect --format {{.NetworkSettings.IPAddress}}  cuplatform_mysql_1) $MYSQL_ROOT_PASSWORD $(docker inspect --format {{.NetworkSettings.IPAddress}} cuplatform_redis_1) > /home/admincu/cloudunit/monitoring_scripts/cloudunitmonitor.log

	rm $LOCK_CM
fi


