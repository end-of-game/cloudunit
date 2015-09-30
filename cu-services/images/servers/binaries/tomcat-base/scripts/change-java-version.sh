#!/bin/bash

set -x

export env_file=/etc/environment
export new_java_version=/cloudunit/java/$1
export new_java_version=$( echo $new_java_version | sed 's/\//\\\//g')

# load the environment context
source /etc/environment

#stop tomcat server
echo su - $CU_USER -c "/cloudunit/scripts/cu-stop.sh"
su - $CU_USER -c "/cloudunit/scripts/cu-stop.sh"

sed -i -e 's/^JAVA_HOME=.*$/JAVA_HOME="'$new_java_version'"/g' $env_file
if [ $? = 0 ];then
        echo "SUCCESS : " ${BASH_SOURCE[0]} - switch to java $1
else
        echo "ERROR : " ${BASH_SOURCE[0]}
fi

# reload the env to use the update
source /etc/environment

#Wait for server shutdown
su - $CU_USER -c "/ps dcloudunit/scripts/waiting-for-shutdown.sh java 30"

# The server is down. We clean the logs
# because they are stored into ElasticSearch
rm -rf /cloudunit/appconf/logs/*

#restart the server
su - $CU_USER -c "/cloudunit/scripts/cu-start.sh"