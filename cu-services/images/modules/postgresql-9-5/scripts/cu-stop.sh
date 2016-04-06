#!/usr/bin/env bash

set -x

WAITFOR=20
count=0
RETURN=1
until [ "$RETURN" -eq "0" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for PostgreSQL stop\n"
	service postgresql stop
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "PostgreSQL is stoped"

