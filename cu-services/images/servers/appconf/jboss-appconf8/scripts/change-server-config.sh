#!/bin/bash

export MEMORY_VALUE=$1
export JVM_OPTIONS=$2

#stop jboss server
su - $CU_USER -c "$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command=:shutdown"

#Wait for server shutdown
su - $CU_USER -c "/cloudunit/scripts/waiting-for-shutdown.sh java 30"

# The server is down. We clean the logs
# because they are stored into ElasticSearch
rm -rf $JBOSS_HOME/standalone/logs/*

sed -i 's/^JAVA_OPTS=.*$/JAVA_OPTS="-Xms'$MEMORY_VALUE'm -Xmx'$MEMORY_VALUE'm -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true '"$2"'"/g' /cloudunit/appconf/standalone.conf

source /etc/environment

#restart the server
su - $CU_USER -c "nohup $JBOSS_HOME/bin/standalone.sh -P=/etc/environment -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0<&- &>/dev/null &"

# test du démarrage de jboss
/cloudunit/scripts/test-jboss-start.sh







