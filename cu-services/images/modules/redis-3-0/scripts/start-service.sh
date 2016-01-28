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

# Premier demarrage
if [ ! -f /cloudunit/database/init-service-ok ]; then
	# Ajout de l'utilisateur et modif du home directory
	useradd $CU_SSH_USER && echo "$CU_SSH_USER:$CU_ROOT_PASSWORD" | chpasswd && echo "root:$CU_ROOT_PASSWORD" | chpasswd
	usermod -d $CU_USER_HOME $CU_SSH_USER
	#Ajout du Shell à l'utilisateur
	usermod -s /bin/bash $CU_SSH_USER

	cp /root/.bashrc $CU_USER_HOME
	cp /root/.profile $CU_USER_HOME

	touch /cloudunit/database/init-service-ok
fi

# Le mdp est passé à redis
sed -i "s|# requirepass foobared|requirepass $CU_PASSWORD|" /etc/redis/redis.conf

# Démarrage de shh
service ssh start

# Attente du démarrage de ssh pour confirmer au manager
until [ "`nc -z localhost 22 && echo $?`" -eq "0" ]
do
	echo -n -e "\nwaiting for ssh to start";
	sleep 1
done

# Démarrage de apache et redis
#source /etc/apache2/envvars && /usr/sbin/apache2 -DFOREGROUND&
redis-server /etc/redis/redis.conf &

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

tailf /var/log/faillog
