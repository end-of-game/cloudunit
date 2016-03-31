#!/bin/bash

# Ajout des variables d'environnement
export CU_USER=$1
export CU_PASSWORD=$2
export CU_DATABASE_NAME=$3
export CU_ROOT_PASSWORD=$4
export CU_SSH_USER=$5

# Database password for Manager
export MANAGER_DATABASE_PASSWORD=$6
# To do difference between main and test env
export ENV_EXEC=$7
export CU_DATABASE_DNS=$8

if [ $ENV_EXEC = "integration" ];
then
    export MYSQL_ENDPOINT=cuplatform_testmysql_1.mysql.cloud.unit
else
    export MYSQL_ENDPOINT=$CU_DATABASE_DNS
fi

## Si l'initialisation a été déja faite, il faut démarrer Postgres
if [ ! -f /init-service-ok ]; then

	# Préparation du dossier de données de postgres
	mv /var/lib/postgresql/9.4/main /cloudunit/database
	chown postgres:postgres -R /cloudunit/database
	chmod 0700 -R /cloudunit/database

	useradd $CU_SSH_USER && echo "$CU_SSH_USER:$CU_ROOT_PASSWORD" | chpasswd 

	## TODO : comment
	echo "root:$CU_ROOT_PASSWORD" | chpasswd

	chmod 600 -R /etc/ssh
	/usr/sbin/sshd

	# Ajout de l'utilisateur / creation et  modif du home directory
	mkdir -p $CU_USER_HOME/.ssh
	usermod -d $CU_USER_HOME $CU_SSH_USER

	#Ajout du Shell à l'utilisateur
	usermod -s /bin/bash $CU_SSH_USER

	## TODO : comment
	cp /root/.bashrc $CU_USER_HOME
	cp /root/.profile $CU_USER_HOME

	## Création d'un superadmin 'docker' pour postgres. On n'utilise pas 'root'
	/etc/init.d/postgresql start
	su - postgres -c "psql --command \"CREATE USER docker WITH SUPERUSER PASSWORD '$CU_ROOT_PASSWORD';\""
	su - postgres -c "createdb -O docker docker"
	/etc/init.d/postgresql stop
	
	# Transformation des variables en variables d'environnement
	chown -R $CU_SSH_USER:$CU_SSH_USER $CU_USER_HOME

	sed -i -e"s:deny from all:# deny from all:g" /etc/apache2/conf.d/phppgadmin
	sed -i -e"s:# allow from all:allow from all:g" /etc/apache2/conf.d/phppgadmin

	/etc/init.d/postgresql start
	/usr/sbin/apachectl start

	## SURTOUT NE PAS SUPPRIMER LA TEMPO !
	sleep 5
	## Création du compte généré dynamiquement côté Java
	psql -U docker --command "CREATE USER $CU_USER WITH SUPERUSER PASSWORD '$CU_PASSWORD'"
	## Création de la base de données du compte et association avec -O (owner) de la BDD et du compte
	su - postgres -c "createdb -O $CU_USER $CU_DATABASE_NAME"
	
	touch /init-service-ok
else 
    # mount /dev/pts
	/usr/sbin/sshd
	/etc/init.d/postgresql start
	/usr/sbin/apachectl start
fi

RETURN=1
# Attente du démarrage du processus sshd pour confirmer au manager
until [ "$RETURN" -eq "0" ];
do
	echo -n -e  "\nWaiting for ssh\n"
	nc -z localhost 22
	RETURN=$?
	sleep 1
done

# Attente du démarrage de postgre
RETURN=1
until [ "$RETURN" -eq "0" ];
do
	nc -z localhost 5432
	RETURN=$?
	echo -n -e "\nwaiting for postgre to start"
	sleep 1
done

/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME START $MANAGER_DATABASE_PASSWORD

tail -f /init-service-ok
