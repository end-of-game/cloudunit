#!/bin/bash

LOG_FILE=/home/admincu/cloudunit/cu-services/pull-log
CACHE=false

if [ -f $LOG_FILE ]; then
	rm $LOG_FILE
fi

if [ -z "$(git describe --exact-match --tags 2>/dev/null)" ]; then
	GIT_TAG=latest
else
	GIT_TAG=`git describe --exact-match --tags 2>/dev/null`
fi

while read line
do
	image=`echo $line | cut -d" " -f 1`
	echo -e "\nPulling image $image."
	docker pull $image:$GIT_TAG
	docker images | grep $image
	return=$?
	if [ "$return" -eq "0" ]; then
		echo -e "\nThe docker image $image:$GIT_TAG has been correctly pulled.\n" >> $LOG_FILE
	else
		echo -e "\nPROBLEM: the docker image $image:$GIT_TAG has not been pulled!\n" >> $LOG_FILE
	fi
done < cu-images
