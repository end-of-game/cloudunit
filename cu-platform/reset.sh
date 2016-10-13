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

echo -e "\nRemoving containers\n"
docker rm -v $(docker ps -a | grep "Dead" | awk '{print $1}')
docker rm -v $(docker ps -q --filter="status=exited")
docker rm -vf $(docker ps -aq --filter "label=origin=cloudunit")
docker rm -vf cuplatform_mysqldata_1
docker rm -vf cuplatform_mysql_1
docker rm -vf cuplatform_testmysqldata_1
docker rm -vf cuplatform_testmysql_1
docker rm -vf cuplatform_redis_1
docker rm -vf cuplatform_hipache_1
docker rm -vf cuplatform_dnsdock_1

# delete all NONE images
docker rmi $(docker images | grep "<none>" | awk '{print $3}')
docker rmi $(docker images | grep "johndoe" | awk '{print $3}')

# delete all volumes
docker volume rm  $(docker volume ls -q)

docker rm -f $(docker ps -aq --filter "label=origin=cloudunit")

echo -e "\nStarting the platform\n"
/home/vagrant/cloudunit/cu-platform/start-platform-dev.sh reset

echo -e "\nChanging directory\n"
cd /home/vagrant/cloudunit/cu-services

echo -e "\nCurrent directory: `pwd`\n"

echo -e "\nRunning services\n"
/home/vagrant/cloudunit/cu-services/run-services.sh




