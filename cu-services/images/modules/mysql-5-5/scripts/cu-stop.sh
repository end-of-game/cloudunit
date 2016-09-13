#!/usr/bin/env bash

set -x

WAITFOR=20
count=0
RETURN=1

kill -s SIGTERM $(pidof mysqld)

until [ "$RETURN" -eq "1" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for Mysql stop\n"
    nc -z localhost 3306
	RETURN=$?
	sleep 1
	let count=$count+1;
done

/etc/init.d/apache2 stop
echo "Mysql is stoped"

