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

if [ $ENV_EXEC = "integration" ];
then
    export MYSQL_ENDPOINT=cuplatform_testmysql_1.mysql.cloud.unit
else
    export MYSQL_ENDPOINT=$CU_DATABASE_DNS
fi

MYSQL_CMD1="mysql -h127.0.0.1 -P3306 -uroot -proot -e 'select 1 from dual;;'"
MYSQL_CMD2="mysql -h127.0.0.1 -P3306 -uroot -p$CU_PASSWORD -e 'select 1 from dual;;'"

if [ ! -f /cloudunit/database/init-service-ok ]; then

	# Création de l'utilisateur ssh (johndoe) et définition du password
	useradd -d $CU_USER_HOME -s /bin/bash $CU_USERNAME_SSH
	echo "$CU_USERNAME_SSH:$CU_ROOT_PASSWORD" | chpasswd && echo "root:$CU_ROOT_PASSWORD" | chpasswd

	# Création du MYSQL_HOME / transfert de l'arborescence MySQL
	cp -rfp /var/lib/mysql/* $CU_DATABASE_HOME
	chown -R mysql:mysql $CU_DATABASE_HOME

fi

	# Démarrage de mysql
	/usr/sbin/mysqld&

if [ ! -f /cloudunit/database/init-service-ok ]; then

	# Attente du démarrage de mysql
	RETURN=1
	until [ "$RETURN" -eq "0" ];
	do	
		echo -n -e "\nWaiting for mysql\n"
		eval "$MYSQL_CMD1"
		RETURN=$?
		sleep 1
	done


	# Création de l'utilisateur client MySQL (admin***)
	mysql -u root --password=root -e 'GRANT ALL PRIVILEGES ON *.* TO '$CU_USER'@"localhost" IDENTIFIED BY "'$CU_PASSWORD'" WITH GRANT OPTION; GRANT ALL PRIVILEGES ON *.* TO '$CU_USER'@"%" IDENTIFIED BY "'$CU_PASSWORD'" WITH GRANT OPTION; FLUSH PRIVILEGES; CREATE DATABASE IF NOT EXISTS `'$CU_DATABASE_NAME'`;ALTER DATABASE `'$CU_DATABASE_NAME'` charset=utf8;'

	mysql -u $CU_USER --password=$CU_PASSWORD -e 'UPDATE mysql.user SET password=PASSWORD("'$CU_PASSWORD'") WHERE user="root";'

	touch /cloudunit/database/init-service-ok
else

	echo "SECOND APPEL !"

	# Attente du démarrage de mysql
	RETURN=1
	until [ "$RETURN" -eq "0" ];
	do	
		echo -n -e "\nWaiting for mysql\n"
		eval "$MYSQL_CMD2"
		RETURN=$?
		sleep 1
	done

fi


# Lancement de ssh et apache
/usr/sbin/sshd&
source /etc/apache2/envvars && /usr/sbin/apache2 -DFOREGROUND&

# Attente du démarrage du processus sshd pour confirmer au manager
until [ "$RETURN" -eq "0" ];
do	
	echo -n -e  "\nWaiting for ssh\n"
	nc -z localhost 22
	RETURN=$?
	sleep 1
done

/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME START $MANAGER_DATABASE_PASSWORD

tailf /var/log/faillog

