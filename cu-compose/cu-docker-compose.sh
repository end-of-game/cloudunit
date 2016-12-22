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

#!/usr/bin/env bash

##
##
## FUNCTIONS
##
##
function check-env {
  if [ -f .env ]; then
    source .env
    echo "env File already exist would you like to use $CU_DOMAIN as domain name ? [y/n]"
    if [ "$1" != "-y" ]; then
        read PROD_ASW
        if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
            echo "Entrer y ou n!"
            exit 1
        elif [ "$PROD_ASW" = "n" ]; then
            generate-env
        fi
    fi
  else
    generate-env
  fi
}

function generate-env {
  echo "Enter CU Domain :"
  read CU_DOMAIN
  echo "CU_DOMAIN=$CU_DOMAIN" >> .env
  echo "Enter CU Sub Domain :"
  read CU_SUB_DOMAIN
  echo "CU_SUB_DOMAIN=$CU_SUB_DOMAIN" >> .env
  source .env
  echo "Enter CU Manager domain : [ default to admin."$CU_DOMAIN" ]"
  read CU_MANAGER_DOMAIN
  if [ -n "$CU_MANAGER_DOMAIN" ]; then
    echo "CU_MANAGER_DOMAIN=$CU_MANAGER_DOMAIN" >> .env
  else
    echo "CU_MANAGER_DOMAIN=admin.$CU_MANAGER_DOMAIN" >> .env
  fi
  echo "Enter CU Gitlab domain : [ default to gitlab."$CU_DOMAIN" ]"
  read CU_GITLAB_DOMAIN
  if [ -n "$CU_GITLAB_DOMAIN" ]; then
    echo "CU_GITLAB_DOMAIN=$CU_GITLAB_DOMAIN" >> .env
  else
    echo "CU_GITLAB_DOMAIN=gitlab.$CU_DOMAIN" >> .env
  fi
  echo "Enter CU Jenkins domain : [ default to jenkins."$CU_DOMAIN" ]"
  read CU_JENKINS_DOMAIN
  if [ -n "$CU_JENKINS_DOMAIN" ]; then
    echo "CU_JENKINS_DOMAIN=$CU_JENKINS_DOMAIN" >> .env
  else
    echo "CU_JENKINS_DOMAIN=jenkins.$CU_DOMAIN" >> .env
  fi
  echo "Enter CU Kibana domain : [ default to kibana."$CU_DOMAIN" ]"
  read CU_KIBANA_DOMAIN
  if [ -n "$CU_KIBANA_DOMAIN" ]; then
    echo "CU_KIBANA_DOMAIN=$CU_KIBANA_DOMAIN" >> .env
  else
    echo "CU_KIBANA_DOMAIN=kibana.$CU_DOMAIN" >> .env
  fi
  echo "Enter CU Letschat domain : [ default to letschat."$CU_DOMAIN" ]"
  read CU_LETSCHAT_DOMAIN
  if [ -n "$CU_LETSCHAT_DOMAIN" ]; then
    echo "CU_LETSCHAT_DOMAIN=$CU_LETSCHAT_DOMAIN" >> .env
  else
    echo "CU_LETSCHAT_DOMAIN=letschat.$CU_DOMAIN" >> .env
  fi
  echo "Enter CU Nexus domain : [ default to nexus."$CU_DOMAIN" ]"
  read CU_NEXUS_DOMAIN
  if [ -n "$CU_NEXUS_DOMAIN" ]; then
    echo "CU_NEXUS_DOMAIN=$CU_NEXUS_DOMAIN" >> .env
  else
    echo "CU_NEXUS_DOMAIN=nexus.$CU_DOMAIN" >> .env
  fi
  echo "Enter CU Sonar domain : [ default to sonar."$CU_DOMAIN" ]"
  read CU_SONAR_DOMAIN
  if [ -n "$CU_SONAR_DOMAIN" ]; then
    echo "CU_SONAR_DOMAIN=$CU_SONAR_DOMAIN" >> .env
  else
    echo "CU_SONAR_DOMAIN=sonar.$CU_DOMAIN" >> .env
  fi
  echo "Enter Elasticsearch URL : [ default to elasticsearch ]"
  read ELASTICSEARCH_URL
  if [ -n "$ELASTICSEARCH_URL" ]; then
    echo "ELASTICSEARCH_URL=$ELASTICSEARCH_URL" >> .env
  else
    echo "ELASTICSEARCH_URL=elasticsearch" >> .env
  fi
  echo "Enter mysql root password : [ default to 'changeit' ]"
  read MYSQL_ROOT_PASSWORD
  if [ -n "$MYSQL_ROOT_PASSWORD" ]; then
    echo "MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD" >> .env
  else
    echo "MYSQL_ROOT_PASSWORD=changeit" >> .env
  fi
  echo "Enter mysql database name : [ default to 'cloudunit' ]"
  read MYSQL_DATABASE
  if [ -n "$MYSQL_DATABASE" ]; then
    echo "MYSQL_DATABASE=$MYSQL_DATABASE" >> .env
  else
    echo "MYSQL_DATABASE=cloudunit" >> .env
  fi
  echo "HOSTNAME=$HOSTNAME" >> .env
}

function with-elk {
    check-env
    docker network create skynet
    docker-compose  -f docker-compose.elk.yml \
                    -f docker-compose.yml \
    up -d
}

function with-elk-and-selenium {
    docker network create skynet
    docker-compose  -f docker-compose.elk.yml \
                    -f docker-compose.selenium.yml \
                    -f docker-compose.yml \
    up -d
}

function reset {
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


    for container in $(docker ps -aq --format '{{.Names}}' --filter "label=origin=application"); do
      echo "Delete applicative container "$container
      docker rm -f $container
      docker volume rm $container
      docker volume ls -qf dangling=true | xargs -r docker volume rm
    done
    docker-compose  -f docker-compose.elk.yml -f docker-compose.selenium.yml -f docker-compose.yml kill
    docker-compose  -f docker-compose.elk.yml -f docker-compose.selenium.yml -f docker-compose.yml rm -f
    docker volume rm cucompose_elasticsearch-data
    docker volume rm cucompose_gitlab-logs
    docker volume rm cucompose_mysqldata
    docker volume rm cucompose_redis-data
    docker network rm skynet
}

##
##
## MAIN
##
##

case "$1" in

'with-elk')
with-elk
;;

'with-elk-and-selenium')
init && with-elk-and-selenium
;;

'reset')
reset
;;


*)
echo ""
echo "Usage $0 "
echo "Example : $0 with-elk"
echo "Choice between : "
echo "                    with-elk"
echo "                    with-elk-and-selenium"
echo "                    reset"
echo ""
;;

esac
