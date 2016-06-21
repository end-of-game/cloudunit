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

source /etc/environment

# Callback bound to the application stop
terminate_handler() {
    /cloudunit/scripts/cu-stop.sh
    CODE=$?
    echo "CODE : " $CODE
    if [[ "$CODE" -eq "0" ]]; then
        $JAVA_HOME/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER FAIL $MANAGER_DATABASE_PASSWORD
    else
        $JAVA_HOME/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER STOP $MANAGER_DATABASE_PASSWORD
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

MAX=45

if [ ! -f /init-service-ok ];
then
   	useradd -m $CU_USER && echo "$CU_USER:$CU_PASSWORD" | chpasswd && echo "root:$CU_PASSWORD" | chpasswd
	usermod -s /bin/bash $CU_USER

	$JBOSS_HOME/bin/add-user.sh --silent=true $CU_USER $CU_PASSWORD

	echo  "CU_USER=$CU_USER" >> /etc/environment
	echo  "CU_PASSWORD=$CU_PASSWORD" >> /etc/environment
	echo  "CU_REST_IP=$CU_REST_IP" >> /etc/environment
	echo  "CU_DATABASE_NAME=$CU_DATABASE_NAME" >> /etc/environment
	echo  "JAVA_HOME=$JAVA_HOME" >> /etc/environment
	echo  "PATH=$JAVA_HOME/bin:$PATH" >> /etc/environment
	echo  "JBOSS_HOME=$CU_HOME/wildfly" >> /etc/environment
	echo  "JAVA_OPTS=-Xms96m -Xms512m -Xmx512m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true" >> /etc/environment
fi

cat /etc/environment
source /etc/environment
/usr/sbin/sshd
chown -R $CU_USER:$CU_USER /cloudunit/appconf/wildfly
/cloudunit/scripts/cu-start.sh
RETURN=$?

# ########################
if [ "$RETURN" -eq  "1" ];
then
    $JAVA_HOME/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER FAIL $MANAGER_DATABASE_PASSWORD
else
    $JAVA_HOME/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER $MYSQL_ENDPOINT $CU_DATABASE_NAME $CU_USER START $MANAGER_DATABASE_PASSWORD
fi

// Initialize the datasource
if [ ! -f /init-service-ok ];
then
    $JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="module add --name=org.mysql --resources=/cloudunit/tmp/mysql-connector-java-5.1.39.jar --dependencies=javax.api,javax.transaction.api"
    $JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-module-name=org.mysql,driver-name=mysql,driver-class-name=com.mysql.jdbc.Driver)"
    touch /init-service-ok
fi

# Blocking step
while true
do
  tail -f /dev/null & wait ${!}
done


