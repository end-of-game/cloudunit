#!/bin/bash

WAITFOR=20
count=0
RETURN=1

httpd-background

until [ "$RETURN" -eq "0" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for Httpd start\n"
    nc -z localhost 80
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "Httpd is started"


