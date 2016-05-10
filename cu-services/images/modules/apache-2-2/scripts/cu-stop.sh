#!/usr/bin/env bash

set -x

WAITFOR=20
count=0
RETURN=1

kill -s SIGTERM $(pidof httpd)

until [ "$RETURN" -eq "1" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for Httpd stop\n"
	nc -z localhost 80
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "Httpd is stopped"

