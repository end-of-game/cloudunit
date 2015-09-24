#!/bin/bash

echo "Voulez-vous vraiment supprimer et recréer les conteneurs de la plate-fome CU? [y/n]"
read PROD_ASW
if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
	echo "Entrer y ou n!"
	exit 1
elif [ "$PROD_ASW" = "n" ]; then
	exit 1
else
	./delete-user-cont.sh
	docker-compose kill && docker-compose rm --force && sudo rm -rf /registry/* /var/log/cloudunit && docker-compose build && ./start-platform.sh
fi
