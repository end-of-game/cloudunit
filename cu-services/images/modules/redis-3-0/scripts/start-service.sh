#!/bin/bash

# Ajout des variables d'environnement
export CU_ROOT_PASSWORD=$1
export CU_REST_IP=$2
export CU_SSH_USER=$3
export CU_PASSWORD=$4
export CU_USERNAME=$5

# Premier demarrage
if [ ! -f /init-service-ok ]; then

	echo $CU_ROOT_PASSWORD > /claire
	# Le mdp est passé à redis
	sed -i "s|# requirepass foobared|requirepass $CU_PASSWORD|" /etc/redis/redis.conf

	# Ajout de l'utilisateur et modif du home directory
	useradd $CU_SSH_USER && echo "$CU_SSH_USER:$CU_ROOT_PASSWORD" | chpasswd && echo "root:$CU_ROOT_PASSWORD" | chpasswd
	usermod -d $CU_USER_HOME $CU_SSH_USER
	#Ajout du Shell à l'utilisateur
	usermod -s /bin/bash $CU_SSH_USER

	cp /root/.bashrc $CU_USER_HOME
	cp /root/.profile $CU_USER_HOME

	touch /init-service-ok
fi

# Démarrage de shh
service ssh start

# Attente du démarrage de ssh pour confirmer au manager
until [ "`nc -z localhost 22 && echo $?`" -eq "0" ]
do	
	echo -n -e "\nwaiting for ssh to start";
	sleep 1
done

curl -sL --connect-timeout 60 --data "containerId=$HOSTNAME" http://$CU_REST_IP:8080/nopublic/module/sshd -o /dev/null

# Démarrage de apache et redis
source /etc/apache2/envvars && /usr/sbin/apache2 -DFOREGROUND&              
redis-server /etc/redis/redis.conf&


# Attente du démarrage de redis pour lancer le webui 
until [ "`nc -z localhost 6379 && echo $?`" -eq "0" ]
do	
	echo -n -e "\nwaiting for redis to start";
	sleep 1
done

# Démarrage du webui
redis-commander --redis-password $CU_PASSWORD --http-auth-username $CU_USERNAME --http-auth-password $CU_PASSWORD
