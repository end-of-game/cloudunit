#!/bin/bash

export TOMCAT_HOME=/cloudunit/appconf
export WAR_NAME=$1
export user=$2
export WAR_PATH=/cloudunit/tmp

#stop tomcat server

ls $TOMCAT_HOME/logs

/cloudunit/scripts/cu-stop.sh /cloudunit/java/jdk1.7.0_55/
/cloudunit/scripts/waiting-for-shutdown.sh java 30

#delete the current app
rm -rf $TOMCAT_HOME/webapps/ROOT
rm -rf $TOMCAT_HOME/webapps/${WAR_NAME%.*ar}
rm -rf $TOMCAT_HOME/work/Catalina/localhost/_

#move the war in webapps
mv $WAR_PATH/$WAR_NAME $TOMCAT_HOME/webapps/ROOT.war

#restart the server
if [ $USER = "root" ];then
	/bin/bash -c "su - $user -c '/cloudunit/scripts/cu-start.sh'"
fi
if [ $USER = $user ];then
	/cloudunit/scripts/cu-start.sh
fi
sleep 2
chown -R $user:$user $TOMCAT_HOME/webapps
