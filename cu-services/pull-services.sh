#!/bin/bash

LOG_FILE=/home/admincu/cloudunit/cu-services/pull-log
CACHE=false

if [ -f $LOG_FILE ]; then
	rm $LOG_FILE
fi

while read line
do
	image=`echo $line | cut -d" " -f 1`
	echo -e "\nPulling image $image."
	docker pull $image 
	docker images | grep $image
	return=$?
	if [ "$return" -eq "0" ]; then
		echo -e "\nThe docker image $image has been correctly pulled.\n" >> $LOG_FILE
	else
		echo -e "\nPROBLEM: the docker image $image has not been pulled!\n" >> $LOG_FILE
	fi
done < cu-images
