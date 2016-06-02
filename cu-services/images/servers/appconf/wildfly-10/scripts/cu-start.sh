#!/bin/bash

set -x

WAITFOR=20
count=0
RETURN=1

echo "################################"
echo "# CLOUDUNIT SERVER START BEGIN #"
echo "################################"

source /etc/environment

su - $CU_USER -c "$JBOSS_HOME/bin/standalone.sh -P=/etc/environment -Djboss.bind.address.management=0.0.0.0 -Djboss.bind.address=0.0.0.0 0<&- &>/dev/null &"

until [ "$RETURN" -eq "0" ] || [ $count -gt $WAITFOR ]
do
	echo -n -e "\nWaiting for WildFly start\n"
    nc -z localhost 9990
	RETURN=$?
	sleep 1
	let count=$count+1;
done

echo "WildFly is started"

echo "##############################"
echo "# CLOUDUNIT SERVER START END #"
echo "##############################"

exit $RETURN

