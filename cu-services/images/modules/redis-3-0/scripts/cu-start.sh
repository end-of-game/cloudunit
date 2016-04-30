#!/bin/bash

WAITFOR=20
count=0
RETURN=1

su -l redis -c "redis-server /etc/redis/redis.conf &"

until [ "$RETURN" -eq "0" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for REDIS start\n"
	nc -z localhost 6379
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "REDIS is started"


