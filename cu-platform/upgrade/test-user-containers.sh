#!/bin/bash
# Y a-t-il des containers "clients"?

CONT_CLIENTS="docker ps -a | egrep -v 'cuplatform_|cloudunit/jboss-8|cloudunit/jboss-7|cloudunit/tomcat-8|cloudunit/tomcat-7|cloudunit/tomcat-6|cloudunit/java|CONTAINER' | cut -d' ' -f 1"

echo "Y a-t-il des conteneurs clients?"

eval $CONT_CLIENTS > cont_clients
wc -l cont_clients

if [ $(eval $CONT_CLIENTS | wc -l) != 0 ]; then
	echo Oui
else
	echo Non
	rm cont_clients
	exit 0
fi

echo "Sont-ils allumés?"

while read line  
do   
	echo $(docker inspect --format {{.State.Running}} $line)
	if [ $(docker inspect --format {{.State.Running}} $line) == "true" ]; then
		echo "ERREUR - Un conteneur client tourne!!!"
		exit 1
	fi
done < cont_clients

echo "OK - Les conteneurs clients sont bien arrêtés."
rm cont_clients
