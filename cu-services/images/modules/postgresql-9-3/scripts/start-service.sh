#!/bin/bash

# Callback bound to the application stop
terminate_handler() {
  service postgresql stop
}

trap 'terminate_handler' SIGTERM

MAX=10

# ###########################
# Waiting for Postgre start #
# ###########################
$CU_SCRIPTS/cu-start.sh

# ############
# First call #
# ############
if [ ! -f /init-service-ok ]; then



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

# Blocking step
while true
do
  tail -f /dev/null & wait ${!}
done
