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

# Attente du démarrage de redis pour lancer le webui
until [ "`nc -z localhost 6379 && echo $?`" -eq "0" ]
do
	echo -n -e "\nwaiting for redis to start";
	sleep 1
done

# WebUI Redis Manager
redis-commander --redis-password $CU_PASSWORD --http-auth-username $CU_USER --http-auth-password $CU_PASSWORD &

# Notification with aget
/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME START $MANAGER_DATABASE_PASSWORD

# Blocking step
while true
do
  tail -f /dev/null & wait ${!}
done
