#!/bin/bash

CONT_NAME=(java)
IMAGE_NAME=(cloudunit/java)
GIT_TAG=latest


for i in 0
do
	docker ps -a | grep ${CONT_NAME[$i]} | grep -q ${IMAGE_NAME[$i]}
	return=$?
	if [ "$return" -eq "0" ]; then
		echo -e "\nThe docker container ${CONT_NAME[$i]} has already been launched.\n"
	else
		echo -e "\nLaunching the docker container ${CONT_NAME[$i]}.\n"
		docker run --name ${CONT_NAME[$i]} ${IMAGE_NAME[$i]}:$GIT_TAG
	fi
done
