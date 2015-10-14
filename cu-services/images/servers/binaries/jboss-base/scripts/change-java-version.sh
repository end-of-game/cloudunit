#!/usr/bin/env bash

export env_file=/etc/environment
export new_java_version=/cloudunit/java/$1
export new_java_version=$( echo $new_java_version | sed 's/\//\\\//g')

# load the environment context
source /etc/environment

#stop jboss server
su - $CU_USER -c "$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command=:shutdown"

#Wait for server shutdown
su - $CU_USER -c "/cloudunit/scripts/waiting-for-shutdown.sh java 30"

sed -i -e 's/^JAVA_HOME=.*$/JAVA_HOME="'$new_java_version'"/g' $env_file
if [ $? = 0 ];then
        echo "SUCCESS : " ${BASH_SOURCE[0]} - switch to java $1
else
        echo "ERROR : " ${BASH_SOURCE[0]}
fi

# reload the env to use the update
source /etc/environment

#Wait for server shutdown
su - $CU_USER -c "/cloudunit/scripts/waiting-for-shutdown.sh java 30"

# The server is down. We clean the logs
# because they are stored into ElasticSearch
rm -rf /cloudunit/appconf/logs/*

#restart the server
su - $CU_USER -c "nohup $JBOSS_HOME/bin/standalone.sh -P=/etc/environment -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0<&- &>/dev/null &"

# test du démarrage de jboss
/cloudunit/scripts/test-jboss-start.sh
