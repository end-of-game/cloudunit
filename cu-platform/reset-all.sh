#!/bin/bash

echo $1
if [ "$1" != "-y" ]; then
	echo "Voulez-vous vraiment supprimer tous les conteneurs et recréer la plate-fome CU ? [y/n]"
	read PROD_ASW
	if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
		echo "Entrer y ou n!"
		exit 1
	elif [ "$PROD_ASW" = "n" ]; then
		exit 1
	fi
fi
	docker kill $(docker ps -aq)
	docker rm -vf $(docker ps -aq)
	cd /home/admincu/cloudunit/cu-platform && sudo rm -rf /registry/* /var/log/cloudunit && ./start-platform.sh 
	cd /home/admincu/cloudunit/cu-services && ./run-services.sh
