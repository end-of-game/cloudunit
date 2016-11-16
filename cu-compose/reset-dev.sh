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
    echo "Are you sure to delete them ? [y/n]"
    read PROD_ASW
    if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
        echo "Entrer y ou n!"
        exit 1
    elif [ "$PROD_ASW" = "n" ]; then
        exit 1
    fi
fi

echo "***************************"
echo -e "Removing containers"
echo "***************************"
docker kill $(docker ps -aq)
docker rm -vf $(docker ps -aq)

echo "***************************"
echo -e "Removing images "
echo "***************************"
docker rmi $(docker images | grep "<none>" | awk '{print $3}')
docker rmi $(docker images | grep "johndoe" | awk '{print $3}')

echo "***************************"
echo -e "Deleting FS data volumes"
echo "***************************"
sudo rm -rf /srv/cu-elk
sudo rm -rf /home/vagrant/mysql_home/
sudo rm -rf /home/vagrant/testmysql_home/

echo "*******************************"
echo -e "Deleting all docker volumes"
echo "*******************************"
docker volume rm $(docker volume ls -q)

echo "*******************************"
echo -e "Starting..."
echo "*******************************"
docker-compose  --file docker-compose.dev.yml \
                --file docker-compose.elk.yml \
                --file docker-compose.test.yml \
                up -d



