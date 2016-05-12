#!/bin/bash

set -x

export CU_USER=$1
export CU_PASSWORD=$2
export CU_REST_IP=$3	
export CU_DATABASE_NAME=$4
export CU_HOME=/cloudunit/appconf
export JAVA_HOME=/cloudunit/java/$5
export MANAGER_DATABASE_PASSWORD=$6
export ENV_EXEC=$7

# Callback bound to the application stop
terminate_handler() {
    kill -s SIGTERM $(pidof apache2)
    CODE=$?
    if [[ "$CODE" -eq "0" ]]; then
        /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER FAIL $MANAGER_DATABASE_PASSWORD
    else
        /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER STOP $MANAGER_DATABASE_PASSWORD
    fi
    exit $CODE;
}

trap 'terminate_handler' SIGTERM

# ENVOI NOTIFICATION CHANGEMENT DE STATUS
if [ $ENV_EXEC = "integration" ];
then
    export MYSQL_ENDPOINT=cuplatform_testmysql_1.mysql.cloud.unit
else
    export MYSQL_ENDPOINT=cuplatform_mysql_1.mysql.cloud.unit
fi


MAX=10

if [ ! -f /init-service-ok ]; then
   	useradd $CU_USER && echo "$CU_USER:$CU_PASSWORD" | chpasswd && echo "root:$CU_PASSWORD" | chpasswd
	useradd -G www-data $CU_USER
	touch /init-service-ok
fi

service apache2 start

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
    /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER FAIL $MANAGER_DATABASE_PASSWORD
else
    /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER START $MANAGER_DATABASE_PASSWORD
fi


# Blocking step
while true
do
  tail -f /dev/null & wait ${!}
done


