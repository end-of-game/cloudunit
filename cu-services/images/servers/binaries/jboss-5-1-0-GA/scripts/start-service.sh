#!/bin/bash

export ENV_FILE="/etc/environment"
# Ajout des variables d'environnement
export CU_USER=$1
export CU_PASSWORD=$2
export CU_REST_IP=$3	
export CU_DATABASE_NAME=$4
export JAVA_HOME=/cloudunit/java/$5
export JBOSS_HOME=/cloudunit/binaries

if [ ! -f /init-service-ok ]; then

	#liens 
	echo "PREMIER APPEL !!!"
	ln -s /cloudunit/appconf/server /cloudunit/binaries/server
	# variable signalant au manager quels scripts lancer
	step=start

	#################
	# PREMIER APPEL #
	#################
	echo "Start Services and configure password for $1" 
	# Transforme les variables en variables d'environnements
	echo "CU_USER=$CU_USER" >> $ENV_FILE
	echo "CU_PASSWORD=$CU_PASSWORD" >> $ENV_FILE
	echo "CU_REST_IP=$CU_REST_IP" >> $ENV_FILE
	echo "CU_DATABASE_NAME=$CU_DATABASE_NAME" >> $ENV_FILE
	echo "JAVA_HOME=$JAVA_HOME" >> $ENV_FILE

	# Ajout de l'utilisateur et modif du home directory
	useradd $1 && echo "$CU_USER:$CU_PASSWORD" | chpasswd && echo "root:$CU_PASSWORD" | chpasswd

	# GENERATION CLES SSH POUR LIEN AVEC MODULES
	ssh-keygen -t rsa -N "" -f /root/.ssh/id_rsa
	mkdir -p $CU_USER_HOME/.ssh
	cp /root/.ssh/id_rsa.pub $CU_USER_HOME/.ssh
	cp /root/.ssh/id_rsa $CU_USER_HOME/.ssh
	cp /root/.bashrc $CU_USER_HOME
	cp /root/.profile $CU_USER_HOME

	# Affection du homedirectory à l'utilisateur unix
	usermod -d $CU_USER_HOME $CU_USER

	# Ajout du Shell à l'utilisateur
	usermod -s /bin/bash $CU_USER

	# Création de l'utilisateur admin de JBOSS
	echo "+-- Add Jboss admin user"
	sed --in-place "s/admin=admin/$CU_USER=$CU_PASSWORD/" $JBOSS_HOME/server/default/conf/props/jmx-console-users.properties
	sed --in-place "s/admin=/$CU_USER=/" $JBOSS_HOME/server/default/conf/props/jmx-console-roles.properties 

	# Fin initialisation
	touch /init-service-ok
else
	echo "SECOND APPEL !!!"

	# variable signalant au manager quels scripts lancer
	step=restart
fi

# Le montage /cloudunit n'appartient qu'à l'utilisateur créé
chown -R $CU_USER:$CU_USER /cloudunit

# Lancement de ssh et jboss
/usr/sbin/sshd&
su - $CU_USER -c "$JBOSS_HOME/bin/run.sh -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0&"

# Attente du démarrage de jboss
/cloudunit/scripts/test-jboss-start.sh

su - $CU_USER -c "tailf $JBOSS_HOME/server/default/log/*.log&"

# Attente du démarrage du processus sshd pour confirmer au manager
RETURN=1
until [ "$RETURN" -eq "0" ];
do
	echo -n -e "\nWaiting for ssh to start\n"
	nc -z localhost 22
	RETURN=$?
	sleep 1
done

# ENVOIE DE REST AU MANAGER 
# /!\ sale le CU_DATABASE_NAME est utilisé pour renseigner le nom de l'appli

/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar SERVER cuplatform_mysql_1.mysql.cloud.unit $CU_DATABASE_NAME $CU_USER START

tailf /var/log/faillog
