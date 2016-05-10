#!/usr/bin/env bash

set -x

WAITFOR=20
count=0
RETURN=1

kill -s SIGTERM $(pidof redis-server)

until [ "$RETURN" -eq "1" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for REDIS stop\n"
	nc -z localhost 6379

	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "REDIS is stoped"

