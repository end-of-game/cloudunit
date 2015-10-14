#!/bin/bash
# remove old know_hosts file and change value of env var CU_SERVERS_IP

NEW_VALUE=$1

rm $CU_USER_HOME/.ssh/known_hosts
sed -i 's/^CU_SERVERS_IP=.*$/CU_SERVERS_IP='$NEW_VALUE'/g' /etc/environment

if [ $? = 0 ];then
	echo "SUCCESS : " ${BASH_SOURCE[0]}
else
	echo "ERROR : " ${BASH_SOURCE[0]}
	exit 1
fi
