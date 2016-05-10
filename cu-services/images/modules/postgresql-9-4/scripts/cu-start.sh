#!/bin/bash

WAITFOR=20
count=0
RETURN=1

service postgresql start

until [ "$RETURN" -eq "0" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for PostgreSQL start\n"
    nc -z localhost 5432
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "PostgreSQL is started"


