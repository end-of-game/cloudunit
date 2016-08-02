#!/bin/bash

export CU_USER=$1
export CU_PASSWORD=$2
export CU_DATABASE_NAME=$3
export CU_ROOT_PASSWORD=$4
export CU_SSH_USER=$5
export MANAGER_DATABASE_PASSWORD=$6
export ENV_EXEC=$7
export CU_DATABASE_DNS=$8

# Callback bound to the application stop
terminate_handler() {
  service postgresql stop
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

MAX=10

/usr/sbin/sshd
/usr/sbin/apachectl start
/etc/init.d/postgresql start

RETURN=1
count=0
# ###########################
# Waiting for Postgre start #
# ###########################
until [[ "$RETURN" -eq "0" ]] || [[ $count -gt $MAX ]]; do
    echo -n -e "\nWaiting for posgresql to start ( $count / $MAX )";
    nc -z localhost 5432
    RETURN=$?
    sleep 1
    let count=$count+1;
done

# ############
# First call #
# ############
if [ ! -f /init-service-ok ]; then
	useradd $CU_SSH_USER && echo "$CU_SSH_USER:$CU_ROOT_PASSWORD" | chpasswd
	echo "root:$CU_ROOT_PASSWORD" | chpasswd

	usermod -s /bin/bash $CU_SSH_USER
	usermod -G postgres $CU_SSH_USER

	su - postgres -c "psql --command \"CREATE USER docker WITH SUPERUSER PASSWORD '$CU_ROOT_PASSWORD';\""
	su - postgres -c "createdb -O docker docker"
    psql -U docker --command "CREATE USER $CU_USER WITH SUPERUSER PASSWORD '$CU_PASSWORD'"
	su - postgres -c "createdb -O $CU_USER $CU_DATABASE_NAME"

    su - postgres -c "psql --command \"CREATE USER datadog WITH SUPERUSER PASSWORD '5bgbzetJQ6nfPVTnnMcMP7SA';\""
    su - postgres -c "grant SELECT ON pg_stat_database to datadog;"

    sed -i -e"s:Require local:# Require local:g" /etc/apache2/conf-enabled/phppgadmin.conf
	sed -i -e"s:deny from all:# deny from all:g" /etc/apache2/conf-enabled/phppgadmin.conf
	sed -i -e"s:# allow from all:allow from all:g" /etc/apache2/conf-enabled/phppgadmin.conf
	service apache2 restart
	touch /init-service-ok
fi

# ####################################
# If postgre is started we notify it #
# ####################################
echo -e -n "END : " + $count " / " $MAX
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
