#!/bin/bash

# Ajout des variables d'environnement
export CU_USER=$1
export CU_PASSWORD=$2
export CU_DATABASE_NAME=$3
export CU_ROOT_PASSWORD=$4
export CU_REST_IP=$5
export CU_USERNAME_SSH=$6


if [ ! -f /cloudunit/database/init-service-ok ]; then

	useradd $CU_USERNAME_SSH && echo "$CU_USERNAME_SSH:$CU_ROOT_PASSWORD" | chpasswd && echo "root:$CU_ROOT_PASSWORD" | chpasswd

	# Création du MYSQL_HOME ET DU HOME_DIRECTORY / transfert de l'arborescence MySQL
	mkdir -p $CU_USER_HOME/.ssh

	# Ajout de l'utilisateur et modif du home directory
	usermod -d $CU_USER_HOME $CU_USERNAME_SSH

	#Ajout du Shell |  l'utilisateur
	usermod -s /bin/bash $CU_USERNAME_SSH

fi

# Démarrage de ssh, puis mongo
service ssh start

numa='numactl --interleave=all'
if $numa true &> /dev/null; then
	set -- $numa mongod 
fi

chown -R mongodb:mongodb /cloudunit/database 
exec gosu mongodb mongod --smallfiles --dbpath /cloudunit/database --auth& > /mongo/mongo.log

# Attente du démarrage de mongo
RETURN=1
until [ "$RETURN" -eq "0" ];
do	
	nc -z localhost 27017
	RETURN=$?
	echo -n -e "\nwaiting for mongo to start"
	sleep 1
done


if [ ! -f /cloudunit/database/init-service-ok ]; then
	# Création de l'utilisateur de la db admin
	sed "s/USER/$CU_USER/g" /mongo/user.js > /mongo/user.tmp.js && sed "s/PASS/$CU_PASSWORD/g" /mongo/user.tmp.js > /mongo/user.js
	mongo mongo/user.js

	touch /cloudunit/database/init-service-ok

fi

# Démarrage de l'UI
lx-mms&

# Attente du démarrage du processus sshd pour confirmer au manager
RETURN=1
until [ "$RETURN" -eq "0" ];
do	
	nc -z localhost 22
	RETURN=$? 
	echo -n -e "\nwaiting for sshd process start"
	sleep 1
done

/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE cuplatform_mysql_1.mysql.cloud.unit $HOSTNAME START

tailf /mongo/mongo.log
