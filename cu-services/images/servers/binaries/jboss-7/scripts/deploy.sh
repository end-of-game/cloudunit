#!/bin/bash

export JBOSS_HOME=/cloudunit/binaries
export WAR_NAME=$1
export user=$2
export WAR_PATH=/cloudunit/tmp



# Delete the current app
rm  $JBOSS_HOME/standalone/deployments/*

# Arrêt de java lancer par jboss
$JBOSS_HOME/bin/jboss-cli.sh -c --command=:shutdown

# Attente de l'extinction de jboss
RETURN=0
until [ "$RETURN" -eq "1" ];
do
	echo -n -e "\nWaiting for jboss to stop\n"
	nc -z localhost 8080
	RETURN=$?
	sleep 1
done


# Move the app in deployment
if [[ $WAR_NAME == *.war ]]; then
	mv $WAR_PATH/$WAR_NAME $JBOSS_HOME/standalone/deployments/ROOT.war
	touch $JBOSS_HOME/standalone/deployments/ROOT.war.dodeploy

fi

if [[ $WAR_NAME == *.ear ]]; then

	mv $WAR_PATH/$WAR_NAME $JBOSS_HOME/standalone/deployments/$WAR_NAME
	touch $JBOSS_HOME/standalone/deployments/$WAR_NAME.dodeploy
fi

# Relance jboss avec options en debranchant les stdout et err
if [ $USER = "root" ];then
	/bin/bash -c "su - $user -c 'nohup $JBOSS_HOME/bin/standalone.sh -P=/etc/environment -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 0<&- &>/dev/null &'"
fi
if [ $USER = $user ];then
	nohup $JBOSS_HOME/bin/standalone.sh -P=/etc/environment -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 0<&- &>/dev/null &
fi

# Attente du démarrage de jboss
/cloudunit/scripts/test-jboss-start.sh

chown -R $user:$user $JBOSS_HOME/standalone/deployments
