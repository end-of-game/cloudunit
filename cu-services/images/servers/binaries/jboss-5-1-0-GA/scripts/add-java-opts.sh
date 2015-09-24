#!/bin/bash

export MEMORY_VALUE=$1
export JAVA_OPTS=$2
export JBOSS_HOME=/cloudunit/binaries


#stop jboss server

sh $JBOSS_HOME/bin/shutdown.sh -S

# Attente de l'extinction de jboss
RETURN=0
until [ "$RETURN" -eq "1" ];
do
	echo -n -e "\nWaiting for jboss to stop\n"
	nc -z localhost 8080
	RETURN=$?
	sleep 1
done

sed -i 's/^JAVA_OPTS=.*$/JAVA_OPTS="-Dfile.encoding=UTF-8 -Xms'$MEMORY_VALUE'm -Xmx'$MEMORY_VALUE'm -XX:MaxPermSize=256m '$JAVA_OPTS'"/g' $JBOSS_HOME/bin/run.conf

#restart the server
sh $JBOSS_HOME/bin/run.sh -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 > /dev/null &

# Attente du démarrage de jboss
/cloudunit/scipts/test-jboss-start.sh
