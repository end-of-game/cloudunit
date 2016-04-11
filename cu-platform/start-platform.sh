# LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
# but CloudUnit is licensed too under a standard commercial license.
# Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
# If you are not sure whether the GPL is right for you,
# you can always test our software under the GPL and inspect the source code before you contact us
# about purchasing a commercial license.

# LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
# or promote products derived from this project without prior written permission from Treeptik.
# Products or services derived from this software may not be called "CloudUnit"
# nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
# For any questions, contact us : contact@treeptik.fr

#!/bin/bash

#Start containers in the right sequence

FROM_RESET=$1

LOCK=/tmp/start-platform.lock
DNS_CMD="dig cloud.unit @172.17.42.1 +short | wc -l"

export CU_SUB_DOMAIN=.$(hostname)

source /home/$USER/.profile
if [ -e "$LOCK" ]; then
	echo "start-platform est déjà en train d'être exécuté"
	exit 1
else
	touch $LOCK

	if [ "$PROFILE" == "dev" ]; then
		echo -e "\nVous utilisez un profile de $PROFILE.\n"
		sed 's/#TO_UNCOMMENT_IF_PROFILE_DEV//' docker-compose.template > docker-compose.yml
	elif [ "$PROFILE" == "prod" ]; then
		echo -e "\nVous utilisez un profile de $PROFILE.\n"
		cp docker-compose.template docker-compose.yml
	else
		echo -e "\nERREUR: RENSEIGNEZ PROFILE=dev/prod DANS .profile !!\n"
	fi

	docker-compose up -d dnsdock

	# Attente du démarrage de dnsdock
	echo -e "\n+++ Dns test +++\n"

	until [ ! $(eval "$DNS_CMD") -eq "0" ];
	do	
		echo -e "\nWaiting for dnsdock : $DNS_CMD \n";
		sleep 1
	done

	docker-compose up -d mysqldata
	docker-compose up -d mysql

	if [ $PROFILE == "dev" ]; then
		docker-compose up -d testmysqldata
		docker-compose up -d testmysql
	fi

	docker-compose up -d hipache
	docker-compose up -d registry

	# Vérification du bon démarrage de dnsdock
	echo -e "\n+++ Dns test inside a container +++\n"

    RETURN=1
    COUNTER=1
	until [ "$RETURN" -eq "0" ];
	do	
        echo Testing $COUNTER time.
        docker exec cuplatform_hipache_1 ping -c 1 cuplatform_dnsdock_1.dnsdock.cloud.unit | grep -q '1 received'
        RETURN=$?
        if [ "$COUNTER" -eq "5" ]; then
            echo Dnsdock has not started correctly. You should restart Docker.
            echo I exit in error !!!
            exit 1
        fi
        let COUNTER=COUNTER+1
		sleep 1
	done

	# Attente du démarrage de mysql
	echo -e "\n+++ Mysql test +++\n"
	RETURN=1

	until [ "$RETURN" -eq "0" ];
	do	
		echo -e "\nWaiting for mysql\n";
		mysql -h$(docker inspect --format {{.NetworkSettings.IPAddress}} cuplatform_mysql_1) -P3306 -uroot -p${MYSQL_ROOT_PASSWORD} -e 'select 1 from dual;;'	--silent &>/dev/null
		RETURN=$?
		sleep 1
	done

    if [ "$FROM_RESET" == "reset" ]; then
    echo "cu-monitor is not launched -- reset mode"
#    else
#	/home/$USER/cloudunit/monitoring_scripts/cu-monitor.sh
    fi

	if [ $PROFILE == "prod" ]; then
		if [ "$CU_KVM" == "true" ]; then
			sed 's/#TO_UNCOMMENT_IF_CU_KVM_TRUE//' docker-compose.template > docker-compose.yml
		fi
		docker-compose up -d tomcat
		docker-compose up -d nginx
	fi	

	docker-compose up -d cadvisor

	rm $LOCK
fi
