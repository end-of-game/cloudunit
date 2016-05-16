#!/bin/bash

WAITFOR=20
count=0
RETURN=1

/usr/sbin/mysqld &

until [ "$RETURN" -eq "0" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for Mysql start\n"
    nc -z localhost 3306
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "Mysql is started"


