#!/bin/bash

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

MAX=30

# Callback bound to the application stop
terminate_handler() {
  echo "/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME STOP $MANAGER_DATABASE_PASSWORD"
  kill -s SIGTERM $(pidof redis-server)
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
	useradd -m $CU_USERNAME_SSH
	usermod -s /bin/bash $CU_USERNAME_SSH
	useradd -G redis $CU_USERNAME_SSH
	echo "$CU_USERNAME_SSH:$CU_ROOT_PASSWORD" | chpasswd
	echo "root:$CU_ROOT_PASSWORD" | chpasswd
	touch /cloudunit/database/init-service-ok
fi

# Le mdp est passé à redis
sed -i "s|# requirepass foobared|requirepass $CU_PASSWORD|" /etc/redis/redis.conf

# Start SSH
service ssh start
# Start redis
chown -R redis /cloudunit/database /cloudunit/backup
su -l redis -c "redis-server /etc/redis/redis.conf &"

count=0

# Attente du démarrage de redis pour lancer le webui
until [[ "$RETURN" -eq "0" ]] || [[ $count -gt $MAX ]];
do
    echo -e "Waiting for redis to start ( $count / $MAX )"
    nc -z localhost 6379
    RETURN=$?
	let count=$count+1;
	sleep 1
done

# WebUI Redis Manager
redis-commander --redis-password $CU_PASSWORD --http-auth-username $CU_USER --http-auth-password $CU_PASSWORD &

# ####################################
# If redis is started we notify it #
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
