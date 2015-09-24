#!/bin/bash

export JBOSS_HOME=/cloudunit/binaries
export WAR_NAME=$1
export user=$2
export WAR_PATH=/cloudunit/tmp

FILE=/cloudunit/home/to_remove

# Arrêt de java lancer par jboss
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


# Delete the current app
while read line
do
	rm $JBOSS_HOME/server/default/deploy/$line
done < $FILE
rm $FILE
# Move the app in deployment
if [[ $WAR_NAME == *.war ]]; then
	rm -rf $JBOSS_HOME/server/default/deploy/ROOT.war
	mv $WAR_PATH/$WAR_NAME $JBOSS_HOME/server/default/deploy/ROOT.war
	echo ROOT.war >> $FILE 
fi

if [[ $WAR_NAME == *.ear ]]; then

	mv $WAR_PATH/$WAR_NAME $JBOSS_HOME/server/default/deploy/$WAR_NAME
	echo $WAR_NAME >> $FILE 
fi

# Relance jboss avec options en debranchant les stdout et err
if [ $USER = "root" ];then
	/bin/bash -c "su - $user -c 'sh $JBOSS_HOME/bin/run.sh -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 > /dev/null &'"
fi
if [ $USER = $user ];then
	sh $JBOSS_HOME/bin/run.sh -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 > /dev/null &
fi

# test démarrage jboss
/cloudunit/scripts/test-jboss-start.sh

chown -R $user:$user $JBOSS_HOME/server/default/deploy
