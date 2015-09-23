#!/bin/bash

export MEMORY_VALUE=$1

#stop tomcat server

/cloudunit/scripts/cu-stop.sh /cloudunit/java/jdk1.7.0_55/ 


#le server est tomcat et on attend 10 sec                                       
/cloudunit/scripts/waiting-for-shutdown.sh java 30

sed -i 's/^export CATALINA_OPTS=.*$/export CATALINA_OPTS="-Dfile.encoding=UTF-8 -Xms'$MEMORY_VALUE'm -Xmx'$MEMORY_VALUE'm -XX:MaxPermSize=256m '"$2"'"/g' /cloudunit/scripts/cu-start.sh

#restart the server
/cloudunit/scripts/cu-start.sh
