#!/bin/bash

LOG_FILE=/home/admincu/cloudunit/cu-services/build-log
CACHE=true

if [ $CACHE = true ]; then
	BUILD_CMD="docker build --rm -t"
elif [ $CACHE = false ]; then
	BUILD_CMD="docker build --rm --no-cache -t"
fi
rm $LOG_FILE

while read line
do
	image=`echo $line | cut -d" " -f 1`
	echo -e "\n Building image $image."
	$BUILD_CMD $line 
	docker images | grep $image
	return=$?
	if [ "$return" -eq "0" ]; then
		echo -e "\nThe docker image $image has been correctly built.\n" >> $LOG_FILE
	else
		echo -e "\nPROBLEM: the docker image $image has not been built!\n" >> $LOG_FILE
	fi
done < cu-images
