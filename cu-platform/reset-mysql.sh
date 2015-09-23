#!/bin/bash

echo "Voulez-vous vraiment supprimer et recréer les conteneurs mysql de la plate-fome CU? [y/n]"
read PROD_ASW
if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
	echo "Entrer y ou n!"
	exit 1
elif [ "$PROD_ASW" = "n" ]; then
	exit 1
else
	docker-compose kill mysqldata mysql && docker-compose rm mysqldata mysql && docker-compose up -d --allow-insecure-ssl mysqldata mysql
fi
