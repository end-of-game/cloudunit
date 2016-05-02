#!/bin/bash

# Ajout des variables d'environnement
export CU_USER=$1
export CU_PASSWORD=$2
export CU_DATABASE_NAME=$3
export CU_ROOT_PASSWORD=$4
export CU_REST_IP=$5
export CU_USERNAME_SSH=$6

# Database password for Manager
export MANAGER_DATABASE_PASSWORD=$7
# To do difference between main and test env
export ENV_EXEC=$8
export CU_DATABASE_DNS=$9

MAX=10

# Callback bound to the application stop
terminate_handler() {
  kill -s SIGTERM $(pidof mongodb)
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

if [ ! -f /cloudunit/database/init-service-ok ]; then
	useradd $CU_USERNAME_SSH && echo "$CU_USERNAME_SSH:$CU_ROOT_PASSWORD" | chpasswd && echo "root:$CU_ROOT_PASSWORD" | chpasswd
	usermod -d $CU_USER_HOME $CU_USERNAME_SSH
    usermod -s /bin/bash $CU_USERNAME_SSH
fi

# Démarrage de ssh, puis mongo
service ssh start

numa='numactl --interleave=all'
if $numa true &> /dev/null; then
	set -- $numa mongod 
fi

chown -R mongodb:mongodb /cloudunit/database /cloudunit/backup
exec gosu mongodb mongod --smallfiles --dbpath /cloudunit/database --auth & > /mongo/mongo.log

if [ ! -f /cloudunit/database/init-service-ok ]; then
	# Création de l'utilisateur de la db admin
	sed "s/USER/$CU_USER/g" /mongo/user.js > /mongo/user.tmp.js && sed "s/PASS/$CU_PASSWORD/g" /mongo/user.tmp.js > /mongo/user.js
	mongo mongo/user.js
	touch /cloudunit/database/init-service-ok
fi

# Démarrage de l'UI
lx-mms &

RETURN=1
count=0

until [[ "$RETURN" -eq "0" ]] || [[ $count -gt $MAX ]];
do
    echo -n -e "\nwaiting for mongo to start ( $count / $MAX )";
	nc -z localhost 27017
    RETURN=$?
	let count=$count+1;
	sleep 1
done

# ####################################
# If mongo is started we notify it #
# ####################################
echo -e "END : " + $count " / " $MAX
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


