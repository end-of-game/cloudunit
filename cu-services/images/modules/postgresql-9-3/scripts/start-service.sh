#!/bin/bash

# Ajout des variables d'environnement
export CU_USER=$1
export CU_PASSWORD=$2
export CU_DATABASE_NAME=$3
export CU_ROOT_PASSWORD=$4
export CU_SSH_USER=$5

## Si l'initialisation a été déja faite, il faut démarrer Postgres
if [ ! -f /init-service-ok ]; then

	# Préparation du dossier de données de postgres
	mv /var/lib/postgresql/9.3/main /cloudunit/database
	chown postgres:postgres -R /cloudunit/database
	chmod 0700 -R /cloudunit/database

	useradd $CU_SSH_USER && echo "$CU_SSH_USER:$CU_ROOT_PASSWORD" | chpasswd 

	## TODO : A SUPPRIMER QUAND ON PASSERA EN PRODUCTION MODE PUBLIC
	echo "root:$CU_ROOT_PASSWORD" | chpasswd

	chmod 600 -R /etc/ssh
	/usr/sbin/sshd

	# Ajout de l'utilisateur / creation et  modif du home directory
	mkdir -p $CU_USER_HOME/.ssh
	usermod -d $CU_USER_HOME $CU_SSH_USER

	#Ajout du Shell à l'utilisateur
	usermod -s /bin/bash $CU_SSH_USER

	## TODO : voir à quoi cela sert, je comprends pas...
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
	/usr/sbin/apachectl start
	su - postgres -c "/usr/lib/postgresql/9.3/bin/postgres -D /var/lib/postgresql/9.3/main -c config_file=/etc/postgresql/9.3/main/postgresql.conf"	&
	
	## SURTOUT NE PAS SUPPRIMER LA TEMPO !
	sleep 5
	## Création du compte généré dynamiquement côté Java
	psql -U docker --command "CREATE USER $CU_USER WITH SUPERUSER PASSWORD '$CU_PASSWORD'"
	## Création de la base de données du compte et association avec -O (owner) de la BDD et du compte
	su - postgres -c "createdb -O $CU_USER $CU_DATABASE_NAME"
	
	touch /init-service-ok
	## Indispensable car on a lancé en BackGround PostGres

	# ENVOIE DE REST AU MANAGER 
	# /!\ sale le CU_DATABASE_NAME est utilisé pour renseigner le nom de l'appli
	/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE cuplatform_mysql_1.mysql.cloud.unit $HOSTNAME START
	tail -f /init-service-ok

else 
    mount /dev/pts
	/usr/sbin/sshd &
	/usr/sbin/apachectl start &
	su - postgres -c "/usr/lib/postgresql/9.3/bin/postgres -D /var/lib/postgresql/9.3/main -c config_file=/etc/postgresql/9.3/main/postgresql.conf"
	echo "avant curl"
	# ENVOIE DE REST AU MANAGER 
	# /!\ sale le CU_DATABASE_NAME est utilisé pour renseigner le nom de l'appli
	/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE cuplatform_mysql_1.mysql.cloud.unit $HOSTNAME START
	echo "après curl"
	tail -f /init-service-ok		
fi 
