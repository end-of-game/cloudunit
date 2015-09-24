#!/bin/bash

export NEW_MEMORY_VALUE=$1
export JBOSS_HOME=/cloudunit/binaries

#stop jboss server

sh $JBOSS_HOME/bin/jboss-cli.sh --connect command=:shutdown &

# Attente de l'extinction de jboss
RETURN=0
until [ "$RETURN" -eq "1" ];
do
	echo -n -e "\nWaiting for jboss to stop\n"
	nc -z localhost 8080
	RETURN=$?
	sleep 1
done

sed -i 's/^JAVA_OPTS=.*$/JAVA_OPTS="-Dfile.encoding=UTF-8 -Xms'$NEW_MEMORY_VALUE'm -Xmx'$NEW_MEMORY_VALUE'm -XX:MaxPermSize=256m"/g' $JBOSS_HOME/bin/standalone.conf

#restart the server
sh $JBOSS_HOME/bin/standalone.sh -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 > /dev/null &

# Attente du démarrage de jboss
/cloudunit/scripts/test-jboss-start.sh
