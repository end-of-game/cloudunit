#!/bin/bash
#Start containers in the right sequence

LOCK=/tmp/start-platform.lock
SKYDNS_CMD="dig unit @172.17.42.1 +short | wc -l"

export CU_SUB_DOMAIN=.$(hostname)

source /home/admincu/.profile
if [ -e "$LOCK" ]; then
	echo "start-platform est déjà en train d'être exécuté"
	exit 1
else
	touch $LOCK

	if [ "$PROFILE" == "dev" ]; then
		echo -n -e "\nVous utilisez un profile de $PROFILE.\n"
		sed -i 's/#TO_UNCOMMENT_IF_PROFILE_DEV//' docker-compose.yml
	elif [ "$PROFILE" == "prod" ]; then
		echo -n -e "\nVous utilisez un profile de $PROFILE.\n"
	else
		echo -n -e "\nERREUR: RENSEIGNEZ PROFILE=dev/prod DANS .profile !!\n"
	fi

	docker-compose up -d skydns

	# Attente du démarrage de skydns
	echo "Skydns test"

	until [ $(eval "$SKYDNS_CMD") -eq "1" ];
	do	
		echo -n -e "\nWaiting for skydns\n";
		sleep 1
	done

	docker-compose up -d skydock
	docker-compose up -d mysqldata
	docker-compose up -d mysql

	if [ $PROFILE == "dev" ]; then
		docker-compose up -d testmysqldata
		docker-compose up -d testmysql
	fi

	docker-compose up -d hipache
	docker-compose up -d registry

	# Attente du démarrage de mysql
	echo "Mysql test"
	mysql -h$(docker inspect --format {{.NetworkSettings.IPAddress}} cuplatform_mysql_1) -P3306 -uroot -pAezohghooNgaegh8ei2jabib2nuj9yoe -e 'select 1 from dual;;'	
	RETURN=1

	until [ "$RETURN" -eq "0" ];
	do	
		echo -n -e "\nWaiting for mysql\n";
		mysql -h$(docker inspect --format {{.NetworkSettings.IPAddress}} cuplatform_mysql_1) -P3306 -uroot -pAezohghooNgaegh8ei2jabib2nuj9yoe -e 'select 1 from dual;;'	
		RETURN=$?
		sleep 1
	done

	/home/admincu/cloudunit/monitoring_scripts/cu-monitor.sh

	if [ $PROFILE == "prod" ]; then
		if [ "$CU_KVM" == "true" ]; then
			sed -i 's/#TO_UNCOMMENT_IF_CU_KVM_TRUE//' docker-compose.yml
		fi
		docker-compose up -d tomcat
		docker-compose up -d nginx
	fi	

	docker-compose up -d cadvisor

	rm $LOCK
fi
