#!/bin/bash

set -x

export ENV_FILE="/opt/cloudunit/.profile"
source $ENV_FILE

$CATALINA_HOME/bin/catalina.sh start

# ON teste l'écoute de tomcat sur le port 8005
SHUTDOWN_WAIT=20
let kwait=$SHUTDOWN_WAIT 
count=0;
nc -z localhost 8005
RETURN=$?
until [ "$RETURN" -eq "0" ] || [ $count -gt $kwait ]
do
	echo -n -e "\nWaiting for tomcat to listen on shutdown port 8005\n"
	nc -z localhost 8005
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "Tomcat is listening on port 8005"

echo "Waiting for Tomcat start with log trace"
count=0;
RETURN=1
until [ "$RETURN" -eq "0" ] || [ $count -gt $kwait ]
do
        echo -e "\nWaiting for tomcat to start"
        grep 'Server startup in' $CU_LOGS/catalina.out
        RETURN=$?
        sleep 1
        let count=$count+1;
done

echo "Server Tomcat is started"

