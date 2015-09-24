#!/bin/bash

export CATALINA_HOME="/cloudunit/binaries"
export CATALINA_BASE="/cloudunit/appconf"

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

# BUG ENTROPY (KVM ENV)
if [ $count -gt $kwait ]; then
	echo -n -e "\nERROR !!!\n Tomcat didn't start listening on shutdown port 8005 after $SHUTDOWN_WAIT seconds\nEXITING IN ERROR !!!\n"
	kill $(pidof tailf)
	exit 1
fi

echo "Tomcat is listening on port 8005"

echo "Waiting for Tomcat start with log trace"
RETURN=1
until [ "$RETURN" -eq "0" ];
do
        echo -e "\nWaiting for tomcat to start"
        grep 'org.apache.catalina.startup.Catalina.start Server startup in' /cloudunit/appconf/logs/catalina.out
        RETURN=$?
        sleep 1
done

echo "Server Tomcat is started"

