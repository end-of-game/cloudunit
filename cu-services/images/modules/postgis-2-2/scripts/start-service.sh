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

	SQLDIR="/usr/share/postgresql/9.4/contrib/postgis-2.2/"
	RESULT=`su - postgres -c "psql -l | grep postgis | wc -l"`
    if [[ ${RESULT} == '1' ]]
    then
        echo 'Postgis Already There'

        if [[ ${HSTORE} == "true" ]]; then
            echo 'HSTORE is only useful when you create the postgis database.'
        fi
        if [[ ${TOPOLOGY} == "true" ]]; then
            echo 'TOPOLOGY is only useful when you create the postgis database.'
        fi
    else
        echo "Postgis is missing, installing now"
        # Note the dockerfile must have put the postgis.sql and spatialrefsys.sql scripts into /root/
        # We use template0 since we want t different encoding to template1
        echo "Creating template postgis"
        su - postgres -c "createdb template_postgis -E UTF8 -T template0"
        echo "Enabling template_postgis as a template"
        CMD="UPDATE pg_database SET datistemplate = TRUE WHERE datname = 'template_postgis';"
        su - postgres -c "psql -c \"$CMD\""
        echo "Loading postgis extension"
        su - postgres -c "psql template_postgis -c 'CREATE EXTENSION postgis;'"

        if [[ ${HSTORE} == "true" ]]
        then
            echo "Enabling hstore in the template"
            su - postgres -c "psql template_postgis -c 'CREATE EXTENSION hstore;'"
        fi
        if [[ ${TOPOLOGY} == "true" ]]
        then
            echo "Enabling topology in the template"
            su - postgres -c "psql template_postgis -c 'CREATE EXTENSION postgis_topology;'"
        fi

        # Needed when importing old dumps using e.g ndims for constraints
        echo "Loading legacy sql"
        su - postgres -c "psql template_postgis -f $SQLDIR/legacy_minimal.sql"
        su - postgres -c "psql template_postgis -f $SQLDIR/legacy_gist.sql"
        # Create a default db called 'gis' that you can use to get up and running quickly
        # It will be owned by the docker db user
        su - postgres -c "createdb -O $CU_USER -T template_postgis gis"
    fi
    # This should show up in docker logs afterwards
    su - postgres -c "psql -l"

    #PID=`cat /var/run/postgresql/9.4-main.pid`
    #kill -9 ${PID}
    #echo "Postgres initialisation process completed .... restarting in foreground"
    #SETVARS="POSTGIS_ENABLE_OUTDB_RASTERS=1 POSTGIS_GDAL_ENABLED_DRIVERS=ENABLE_ALL"
    #su - postgres -c "$SETVARS $POSTGRES -D $DATADIR -c config_file=$CONF"

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
