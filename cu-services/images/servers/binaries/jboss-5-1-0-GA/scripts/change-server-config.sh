#!/bin/bash

export MEMORY_VALUE=$1
export JBOSS_HOME=/cloudunit/binaries
export MANAGER_URL=$3


# test jboss start
/cloudunit/scripts/test-jboss-start.sh

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

sed -i 's/^JAVA_OPTS=.*$/JAVA_OPTS="-Dfile.encoding=UTF-8 -Xms'$MEMORY_VALUE'm -Xmx'$MEMORY_VALUE'm -XX:MaxPermSize=256m '$2'"/g' $JBOSS_HOME/bin/run.conf

#restart the server
sh $JBOSS_HOME/bin/run.sh -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 > /dev/null &

# test jboss start
/cloudunit/scripts/test-jboss-start.sh
