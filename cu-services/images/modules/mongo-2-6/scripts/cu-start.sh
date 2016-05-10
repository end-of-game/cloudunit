#!/bin/bash

WAITFOR=20
count=0
RETURN=1

exec gosu mongodb mongod --smallfiles --dbpath /cloudunit/database --auth &

until [ "$RETURN" -eq "0" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for Mongo start\n"
    nc -z localhost 27017
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "Mongo is started"


