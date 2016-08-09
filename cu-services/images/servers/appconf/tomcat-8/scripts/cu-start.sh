#!/bin/bash

set -x

export ENV_FILE="/opt/cloudunit/.profile"
source $ENV_FILE

$CATALINA_HOME/bin/catalina.sh start

SHUTDOWN_WAIT=20
RETURN=1
count=0

until [ "$RETURN" -eq "0" ] || [ $count -gt 30 ]
do
        echo -e "\nWaiting for tomcat to start"
        grep 'Server startup in' $CU_LOGS/catalina.out
        RETURN=$?
        sleep 1
        let count=$count+1;
done

echo "Server Tomcat is started"

