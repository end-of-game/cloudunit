#!/bin/bash

CONT_NAME=(java tomcat-6 tomcat-7 tomcat-8 jboss-7 jboss-8 jboss-5-1-0)
IMAGE_NAME=(cloudunit/java cloudunit/tomcat-6 cloudunit/tomcat-7 cloudunit/tomcat-8 cloudunit/jboss-7 cloudunit/jboss-8 cloudunit/jboss-5-1-0)
LOG_FILE=/home/admincu/cloudunit/cu-services/run-log

if [ -f $LOG_FILE ]; then
	rm $LOG_FILE
fi

for i in 0 1 2 3 4 5 6
do
	docker ps -a | grep ${CONT_NAME[$i]} | grep -q ${IMAGE_NAME[$i]}
	return=$?
	if [ "$return" -eq "0" ]; then
		echo -e "\nThe docker container ${CONT_NAME[$i]} has already been launched.\n" >> $LOG_FILE
	else
		echo -e "\nLaunching the docker container ${CONT_NAME[$i]}.\n" >> $LOG_FILE
		docker run --name ${CONT_NAME[$i]} ${IMAGE_NAME[$i]}
	fi
done
