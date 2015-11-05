# LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
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
# Seuls les containers "clients" sont supprimés, sauf malchance sur le nom de l'application

docker ps -a | egrep -v 'cuplatform_|cloudunit/jboss-8|cloudunit/jboss-7|cloudunit/tomcat-8|cloudunit/tomcat-7|cloudunit/tomcat-6|cloudunit/java' | cut -d' ' -f 1 | grep -v CONTAINER > cont_id
NB="wc -l cont_id | cut -d' ' -f 1"

# S'il n'y a pas d'app clientes, on sort.
if [ `eval $NB` == 0 ]; then
	echo "No user container."
	rm cont_id
	exit 0
fi

echo "Do you really want to delete user containers? [y/n]"
read PROD_ASW
if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
	echo "Type y or n!"
	exit 1
elif [ "$PROD_ASW" = "n" ]; then
	exit 1
else

	# Sinon, on les tue puis on les suppprime.
	while read line
	do
		docker kill $line
		docker rm $line
	done < cont_id

	rm cont_id
fi
