#!/bin/bash

export containerApplicatif=$1
export userLogin=$2
export userDbPassword=$3
export applicationName=$4
export userPassword=$5
export containerData=$containerApplicatif"-data"

#Supprime l'existant
(docker ps -a | grep "$containerApplicatif" |awk '{print $1}'| xargs docker kill)
(docker ps -a | grep "$containerData" |awk '{print $1}'| xargs docker kill)

(docker ps -a | grep "$containerApplicatif" |awk '{print $1}'| xargs docker rm -f)
(docker ps -a | grep "$containerData" |awk '{print $1}'| xargs docker rm -f)

#Crée un nouveau container data-db
docker run -d -v /vagrant_cloudunit/cloudunit/backup-$containerApplicatif:/cloudunit/backup --name $containerData -p 22/tcp cloudunit/data-db /cloudunit/scripts/start-service.sh

#fonctionne uniquement s'il n'y a qu'un port d'exposé
ipAddress=$(docker inspect $containerData | grep "IPAddress\": \"" | cut -d '"' -f4 | cut -c 1-100)  

docker run --volumes-from $containerData -p 22/tcp -p 80/tcp -d --name $containerApplicatif cloudunit/mysql-5-5 $userLogin $userDbPassword $applicationName $userPassword



ssh root@$ipAddress "tar xvf /cloudunit/backup/data-db.tar -C /cloudunit/database"
