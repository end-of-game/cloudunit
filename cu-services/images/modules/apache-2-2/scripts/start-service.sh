#!/bin/bash

export CU_USER=$1
export CU_PASSWORD=$2
export CU_DATABASE_NAME=$3
export CU_ROOT_PASSWORD=$4
export CU_SSH_USER=$5
export MANAGER_DATABASE_PASSWORD=$6
export ENV_EXEC=$7
export CU_DATABASE_DNS=$8

# Callback bound to the application stop
terminate_handler() {
    kill -s SIGTERM $(pidof httpd)
    CODE=$?
    if [[ "$CODE" -eq "0" ]]; then
        /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME STOP $MANAGER_DATABASE_PASSWORD
    else
        /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME FAIL $MANAGER_DATABASE_PASSWORD
    fi
    exit $CODE;
}

trap 'terminate_handler' SIGTERM

if [ $ENV_EXEC = "integration" ];
then
    export MYSQL_ENDPOINT=cuplatform_testmysql_1.mysql.cloud.unit
else
    export MYSQL_ENDPOINT=$CU_DATABASE_DNS
fi

MAX=10

httpd-background

RETURN=1
count=0
# ########################
# Waiting for HTTP start #
# ########################
until [[ "$RETURN" -eq "0" ]] || [[ $count -gt $MAX ]]; do
    echo -n -e "\nWaiting for http to start ( $count / $MAX )";
    nc -z localhost 80
    RETURN=$?
    sleep 1
    let count=$count+1;
done

# ########################
# Waiting for HTTP start #
# ########################
echo -e -n "END : " + $count " / " $MAX
if [[ $count -gt $MAX ]]; then
    /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME FAIL $MANAGER_DATABASE_PASSWORD
else
    /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME START $MANAGER_DATABASE_PASSWORD
fi

# Blocking step
while true
do
  tail -f /dev/null & wait ${!}
done
