#!/bin/bash

#test du démarrage de jboss
RETURN=1
until [ "$RETURN" -eq "0" ];
do
	echo -n -e "\nWaiting for jboss to start\n"
	curl http://localhost:9990/console/App.html
	RETURN=$?
	sleep 1
done


