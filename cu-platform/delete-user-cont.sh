#!/bin/bash
# Seuls les containers "clients" sont supprimés, sauf malchance sur le nom de l'application

docker ps -a | egrep -v 'cuplatform_|cloudunit/jboss-8|cloudunit/jboss-7|cloudunit/tomcat-8|cloudunit/tomcat-7|cloudunit/tomcat-6|cloudunit/java' | cut -d' ' -f 1 | grep -v CONTAINER > cont_id
NB="wc -l cont_id | cut -d' ' -f 1"

# S'il n'y a pas d'app clientes, on sort.
if [ `eval $NB` == 0 ]; then
	rm cont_id
	exit 0
fi

# Sinon, on les tue puis on les suppprime.
while read line
do
	docker kill $line
	docker rm $line
done < cont_id

rm cont_id
