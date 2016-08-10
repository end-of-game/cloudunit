#!/bin/bash

set -x

export ENV_FILE="/opt/cloudunit/.profile"
export JAVA_HOME=/opt/cloudunit/java/jdk1.8.0_25

# Callback bound to the application stop
pid=0
term_handler() {
  if [ $pid -ne 0 ]; then
    /opt/cloudunit/scripts/cu-stop.sh
	/opt/cloudunit/scripts/waiting-for-shutdown.sh java 30
  fi
  exit 42;
}

trap 'term_handler' SIGTERM

if [ ! -f /opt/cloudunit/init-service-ok ]; then

	##############
	# First CALL #
	##############
	echo "Start Services and configure password for $1" 

	# Transforme les variables en variables d'environnements
	export CATALINA_OPTS="-Dfile.encoding=UTF-8 -Xms512m -Xmx512m -XX:MaxPermSize=256m"

	echo  "JAVA_HOME=$JAVA_HOME" >> $ENV_FILE
	echo  "CATALINA_OPTS=$CATALINA_OPTS" >> $ENV_FILE
    	source $ENV_FILE
    	
	# mv /tomcat $TOMCAT_HOME
	rm -rf $CU_SOFTWARE/webapps/ROOT $CU_SOFTWARE/webapps/examples $CU_SOFTWARE/webapps/docs

	# Fin initialisation
	touch /opt/cloudunit/init-service-ok

fi

#sourcing des variables d'environment
export ENV_FILE="/opt/cloudunit/.profile"
source $ENV_FILE

# Lancement de tomcat avec Attente du demarrage de tomcat
/opt/cloudunit/scripts/cu-start.sh

pid=`pidof java`

# wait indefinetely
while true
do
  tail -f /dev/null & wait ${!}
done


