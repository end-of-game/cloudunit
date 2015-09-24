#!/bin/bash

CONTAINER=cuplatform_"$2"_1
echo "$CONTAINER"

STATE=$(docker inspect -f='{{.state.Running}}' $CONTAINER)

if  [ "$1" = "CRITICAL" ] || [ "$1" = "WARNING" ]; then
	if [ "$STATE" == "true" ]; then
		cd /home/admincu/cloudunit/cu-platform && docker-compose restart $2
	else	
		cd /home/admincu/cloudunit/cu-platform && docker-compose up -d --no-recreate --allow-insecure-ssl $2
	fi
fi
