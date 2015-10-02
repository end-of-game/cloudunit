# LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

sudo service docker restart ; sleep 10

export FROM_RESET=$1

SKYDNS_CMD="dig unit @172.17.42.1 +short | wc -l"

export CU_SUB_DOMAIN=.$(hostname)
docker-compose up -d skydns

# Attente du démarrage de skydns
echo "Skydns test"

until [ $(eval "$SKYDNS_CMD") -eq "1" ];
do
	echo -n -e "\nWaiting for skydns\n";
	sleep 1
done

docker-compose up -d skydock
docker-compose up -d testmysqldata
docker-compose up -d testmysql
docker-compose up -d hipache
docker-compose up -d registry

# Attente du démarrage de mysql
echo "Mysql test"
mysql -h$(docker inspect --format {{.NetworkSettings.IPAddress}} travis_testmysql_1) -P3307 -uroot -pAezohghooNgaegh8ei2jabib2nuj9yoe -e 'select 1 from dual;;'
RETURN=1

until [ "$RETURN" -eq "0" ];
do
  docker ps -a
	echo -n -e "\nWaiting for mysql\n";
	mysql -h$(docker inspect --format {{.NetworkSettings.IPAddress}} travis_testmysql_1) -P3307 -uroot -pAezohghooNgaegh8ei2jabib2nuj9yoe -e 'select 1 from dual;;'
	RETURN=$?
	sleep 1
done

docker-compose up -d cadvisor
