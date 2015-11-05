#!/bin/bash

FILE=/home/shinken/app_servers
EXIT_CODE=0

./get-mysql-data.sh servers | grep Running | cut -d$'\t' -f 7 > $FILE

while read -r line
do
	CONTAINER=$line
	echo "Checking application server in container $CONTAINER"

	CONTAINER_IP=$(docker inspect --format {{.NetworkSettings.IPAddress}} $CONTAINER)	
	RETURN=$(echo $?)
	
	if [ "$RETURN" != 0 ]; then
		echo "CRITICAL - Container $CONTAINER is not responding. | running_app_server=`wc -l $FILE`;5;10"
		EXIT_CODE=2
	else
		OUTPUT=$(curl --max-time 10 -sS http://$CONTAINER_IP:8080)
		RETURN=$(echo $?)

		if [ "$RETURN" != 0 ]; then
			echo "CRITICAL - Application server from container $CONTAINER is not responding. | running_app_server=`wc -l $FILE`;5;10"
			echo "$RETURN"
			EXIT_CODE=2
		else
			echo "OK"
		fi
	fi

done < "$FILE"

exit $EXIT_CODE
