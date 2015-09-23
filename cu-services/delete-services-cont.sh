#!/bin/bash
# Seuls les containers services sont supprimés.

docker ps -a | grep 'cloudunit/jboss-8\|cloudunit/jboss-7\|cloudunit/tomcat-8\|cloudunit/tomcat-7\|cloudunit/tomcat-6\|cloudunit/java' | cut -d' ' -f 1 > cont_id
NB="wc -l cont_id | cut -d' ' -f 1"

# S'il n'y a pas de cont services, on sort.
if [ `eval $NB` == 0 ]; then
	rm cont_id
	exit 0
fi

# Sinon, on les suppprime.
while read line
do
	docker rm $line
done < cont_id

rm cont_id
