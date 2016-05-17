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
source $HOME/.profile

export CU_SUB_DOMAIN=.$(hostname)

DNS_CMD="dig cloud.unit @172.17.42.1 +short | wc -l"

docker-compose up -d dnsdock
echo -e "\n+++ Dns test +++\n"
until [ ! $(eval "$DNS_CMD") -eq "0" ];
do
    echo -e "\nWaiting for dnsdock : $DNS_CMD \n";
    sleep 1
done

docker-compose up -d mysqldata
docker-compose up -d mysql
docker-compose up -d tomcat

docker-compose up -d hipache

# DNS DOCK
echo -e "\n+++ Dns test inside a container +++\n"

RETURN=1
COUNTER=1
until [ "$RETURN" -eq "0" ];
do
    echo Testing $COUNTER time.
    docker exec cuproduction_hipache_1 ping -c 1 cuproduction_dnsdock_1.dnsdock.cloud.unit | grep -q '1 received'
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
    mysql -h$(docker inspect --format {{.NetworkSettings.IPAddress}} cuproduction_mysql_1) -P3306 -uroot -p${MYSQL_ROOT_PASSWORD} -e 'select 1 from dual;;'	--silent &>/dev/null
    RETURN=$?
    sleep 1
done

docker-compose up -d cadvisor



