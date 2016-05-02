#!/usr/bin/env bash

set -x

WAITFOR=20
count=0
RETURN=1

service postgresql stop

until [ "$RETURN" -eq "1" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for PostgreSQL stop\n"
	nc -z localhost 5432
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "PostgreSQL is stopped"

