# LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
# but CloudUnit is licensed too under a standard commercial license.
# Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
# If you are not sure whether the GPL is right for you,
# you can always test our software under the GPL and inspect the source code before you contact us
# about purchasing a commercial license.

# LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
# or promote products derived from this project without prior written permission from Treeptik.
# Products or services derived from this software may not be called "CloudUnit"
# nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
# For any questions, contact us : contact@treeptik.fr

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
