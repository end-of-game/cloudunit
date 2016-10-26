#!/usr/bin/env bash

set -x

echo "###################################"
echo "# CLOUDUNIT SERVER SHUTDOWN BEGIN #"
echo "###################################"

WAITFOR=20
count=0
RETURN=1

source /etc/environment
$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command=:shutdown

until [ "$RETURN" -eq "1" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for WildFly stop\n"
	nc -z localhost 9990
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "WildFly is stopped"

echo "#################################"
echo "# CLOUDUNIT SERVER SHUTDOWN END #"
echo "#################################"

if [ "$RETURN" -eq "1" ]
then
    exit 0
else
    exit 1
fi

