#!/bin/sh

# Sélection de la donnée à afficher
case "$1" in
	"users")
		SQL_COMMAND='select * from User'
		;;
	"messages")
		SQL_COMMAND='select * from Message'
		;;
	"applications")
		SQL_COMMAND='select * from Application'
		;;
	"modules")
		SQL_COMMAND='select * from Module'
		;;
	"servers")
		SQL_COMMAND='select * from Server'
		;;
	*)
		echo "Usage : ./get-mysql-data.sh users|messages|applications|modules|servers"
		exit 1
		;;
esac

# Construction en 2 étapes de la commande de récupération de données
GLOBAL_COMMAND="mysql -h$(docker inspect --format {{.NetworkSettings.IPAddress}} cuplatform_mysql_1) -P3306 -uroot -pAezohghooNgaegh8ei2jabib2nuj9yoe --database=cloudunit -e"" \"$SQL_COMMAND\""

# Exécution de la comande de récupération des données
GLOBAL_RESULT=$(eval "$GLOBAL_COMMAND")


# Filtrage du résultat pour n'avoir que les lignes de données
FILTERED_RESULT=$(echo "$GLOBAL_RESULT" | tail -n +2)

# Si le résultat n'est pas vide, affichage du résultat
if [ ! -z "$FILTERED_RESULT" ]; then
	echo "$FILTERED_RESULT"
fi
