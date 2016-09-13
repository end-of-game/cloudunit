#!/bin/bash

# Add env variable
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

MAX=30

# Callback bound to the application stop
terminate_handler() {
  /etc/init.d/apache2 stop
  kill -s SIGTERM $(pidof mysqld)
  CODE=$?
  if [[ "$CODE" -eq "0" ]]; then
    /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME STOP $MANAGER_DATABASE_PASSWORD
  else
    /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME FAIL $MANAGER_DATABASE_PASSWORD
  fi
  exit $CODE;
}

trap 'terminate_handler' SIGTERM

if [ ! -f /cloudunit/database/init-service-ok ]; then

	# Create the user and set his password
	useradd -d $CU_USER_HOME -s /bin/bash $CU_USERNAME_SSH
	useradd -G mysql $CU_USERNAME_SSH
	echo "$CU_USERNAME_SSH:$CU_ROOT_PASSWORD" | chpasswd && echo "root:$CU_ROOT_PASSWORD" | chpasswd

	# Change mysql-data directory
	cp -rfp /var/lib/mysql/* $CU_DATABASE_HOME
	chown -R mysql:mysql $CU_DATABASE_HOME
fi

/usr/sbin/mysqld &
/usr/sbin/sshd &
source /etc/apache2/envvars && /usr/sbin/apache2 -DFOREGROUND &

if [ ! -f /cloudunit/database/init-service-ok ]; then
	RETURN=1
	count=0
    until [[ "$RETURN" -eq "0" ]] || [[ $count -gt $MAX ]];
	do
        echo -n -e "\nwaiting for mysql to start ( $count / $MAX )";
	    nc -z localhost 3306
		RETURN=$?
		let count=$count+1;
		sleep 1
	done
	# Create the admin user
	mysql -u root --password=root -e 'GRANT ALL PRIVILEGES ON *.* TO '$CU_USER'@"localhost" IDENTIFIED BY "'$CU_PASSWORD'" WITH GRANT OPTION; GRANT ALL PRIVILEGES ON *.* TO '$CU_USER'@"%" IDENTIFIED BY "'$CU_PASSWORD'" WITH GRANT OPTION; FLUSH PRIVILEGES; CREATE DATABASE IF NOT EXISTS `'$CU_DATABASE_NAME'`;ALTER DATABASE `'$CU_DATABASE_NAME'` charset=utf8;'
	# mysql -u $CU_USER --password=$CU_PASSWORD -e 'UPDATE mysql.user SET password=PASSWORD("'$CU_PASSWORD'") WHERE user="root";'
	touch /cloudunit/database/init-service-ok
fi

count=0
until [[ "$RETURN" -eq "0" ]] || [[ $count -gt $MAX ]];
do
    echo -n -e "\nwaiting for mysql to start ( $count / $MAX )";
	nc -z localhost 3306
    RETURN=$?
	let count=$count+1;
	sleep 1
done

# source /etc/apache2/envvars && /usr/sbin/apache2 -DFOREGROUND &
/etc/init.d/apache2 start

# ####################################
# If mysql is started we notify it #
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

