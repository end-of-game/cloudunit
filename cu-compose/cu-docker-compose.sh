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
    if [ -n "$SILENT_INSTALL" ] || [ "$SILENT_INSTALL" = "no" ]; then
      echo "env File already exist would you like to use $CU_DOMAIN as domain name ? [y/n]"
      if [ "$1" != "-y" ]; then
          read PROD_ASW
          if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
              echo "Entrer y ou n!"
              exit 1
          elif [ "$PROD_ASW" = "n" ]; then
              rm .env
              generate-env
          fi
      fi
    fi
  else
    generate-env
  fi
}

function generate-env {

  echo "# Set CloudUnit deployment Environment" > .env
  echo "" >> .env

  echo "Enter CU Domain :"
  read CU_DOMAIN
  echo "CU_DOMAIN=$CU_DOMAIN" >> .env
  echo "Enter CU Sub Domain : [ no default ]"
  source .env

  echo "Enter CU Manager domain : [ default to https://"$CU_DOMAIN" ]"
  read CU_MANAGER_DOMAIN
  if [ -n "$CU_MANAGER_DOMAIN" ]; then
    echo "CU_MANAGER_DOMAIN=$CU_MANAGER_DOMAIN" >> .env
  else
    echo "CU_MANAGER_DOMAIN=$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Gitlab domain : [ default to https://gitlab-"$CU_DOMAIN" ]"
  read CU_GITLAB_DOMAIN
  if [ -n "$CU_GITLAB_DOMAIN" ]; then
    echo "CU_GITLAB_DOMAIN=$CU_GITLAB_DOMAIN" >> .env
  else
    echo "CU_GITLAB_DOMAIN=gitlab-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Jenkins domain : [ default to https://jenkins-"$CU_DOMAIN" ]"
  read CU_JENKINS_DOMAIN
  if [ -n "$CU_JENKINS_DOMAIN" ]; then
    echo "CU_JENKINS_DOMAIN=$CU_JENKINS_DOMAIN" >> .env
  else
    echo "CU_JENKINS_DOMAIN=jenkins-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Kibana domain : [ default to https://kibana-"$CU_DOMAIN" ]"
  read CU_KIBANA_DOMAIN
  if [ -n "$CU_KIBANA_DOMAIN" ]; then
    echo "CU_KIBANA_DOMAIN=$CU_KIBANA_DOMAIN" >> .env
  else
    echo "CU_KIBANA_DOMAIN=kibana-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU mattermost domain : [ default to https://mattermost-"$CU_DOMAIN" ]"
  read CU_MATTERMOST_DOMAIN
  if [ -n "$CU_MATTERMOST_DOMAIN" ]; then
    echo "CU_MATTERMOST_DOMAIN=$CU_MATTERMOST_DOMAIN" >> .env
  else
    echo "CU_MATTERMOST_DOMAIN=mattermost-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Nexus domain : [ default to https://nexus-"$CU_DOMAIN" ]"
  read CU_NEXUS_DOMAIN
  if [ -n "$CU_NEXUS_DOMAIN" ]; then
    echo "CU_NEXUS_DOMAIN=$CU_NEXUS_DOMAIN" >> .env
  else
    echo "CU_NEXUS_DOMAIN=nexus-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Sonar domain : [ default to https://sonar-"$CU_DOMAIN" ]"
  read CU_SONAR_DOMAIN
  if [ -n "$CU_SONAR_DOMAIN" ]; then
    echo "CU_SONAR_DOMAIN=$CU_SONAR_DOMAIN" >> .env
  else
    echo "CU_SONAR_DOMAIN=sonar-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Prometheus domain : [ default to https://prometheus-"$CU_DOMAIN" ]"
  read CU_PROMETHEUS_DOMAIN
  if [ -n "$CU_PROMETHEUS_DOMAIN" ]; then
    echo "CU_PROMETHEUS_DOMAIN=$CU_PROMETHEUS_DOMAIN" >> .env
  else
    echo "CU_PROMETHEUS_DOMAIN=prometheus-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Alertmanager domain : [ default to https://alertmanager-"$CU_DOMAIN" ]"
  read CU_ALERTMANAGER_DOMAIN
  if [ -n "$CU_ALERTMANAGER_DOMAIN" ]; then
     echo "CU_ALERTMANAGER_DOMAIN=$CU_ALERTMANAGER_DOMAIN" >> .env
  else
    echo "CU_ALERTMANAGER_DOMAIN=alertmanager-$CU_DOMAIN" >> .env
  fi

  echo "Enter CU Grafana domain : [ default to https://grafana-"$CU_DOMAIN" ]"
  read CU_GRAFANA_DOMAIN
  if [ -n "$CU_GRAFANA_DOMAIN" ]; then
    echo "CU_GRAFANA_DOMAIN=$CU_GRAFANA_DOMAIN" >> .env
  else
    echo "CU_GRAFANA_DOMAIN=grafana-$CU_DOMAIN" >> .env
  fi

  echo "Enter Elasticsearch rest API URL if you want to use external database : [ default to internal elasticsearch ]"
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

  echo "Enter mysql root database name : [ default to 'cloudunit' ]"
  read MYSQL_DATABASE
  if [ -n "$MYSQL_DATABSE" ]; then
    echo "MYSQL_DATABASE=$MYSQL_DATABASE" >> .env
  else
    echo "MYSQL_DATABASE=cloudunit" >> .env
  fi

  echo "HOSTNAME=$HOSTNAME" >> .env
  if [ -f /etc/redhat-release ]; then
    echo "TZ=$(sed -n 2p /etc/localtime)" >> .env
  else
    echo "TZ=$(cat /etc/timezone)" >> .env
  fi

  proxy_gitlab="gitlab_rails['env'] = {"

  if [ -n "$http_proxy" ]; then
    echo "http_proxy=$http_proxy" >> .env 
    proxy_gitlab=$proxy_gitlab"\"http_proxy\" => \"$http_proxy\""
    proxy_info=1
  fi
  if [ -n "$https_proxy" ]; then
    echo "https_proxy=$https_proxy" >> .env 
    if [ -n "$proxy_info" ]; then
        proxy_gitlab=$proxy_gitlab", "
    fi
    proxy_gitlab=$proxy_gitlab"\"https_proxy\" => \"$https_proxy\""
    proxy_info=1
  fi
  if [ -n "$ftp_proxy" ]; then
    echo "ftp_proxy=$ftp_proxy" >> .env
    if [ -n "$proxy_info" ]; then
        proxy_gitlab=$proxy_gitlab", "
    fi
    proxy_gitlab=$proxy_gitlab"\"ftp_proxy\" => \"$ftp_proxy\""
    proxy_info=1
  fi
  if [ -n "$no_proxy" ]; then
    echo "no_proxy=$no_proxy,.skynet" >> .env
    if [ -n "$proxy_info" ]; then
        proxy_gitlab=$proxy_gitlab", "
    fi
    proxy_gitlab=$proxy_gitlab"\"no_proxy\" => \"$no_proxy,.skynet\""
    proxy_info=1
  fi
  proxy_gitlab=$proxy_gitlab"}"

  if [ -n "$proxy_info" ]; then
    echo "$proxy_gitlab" >> cu-gitlab-ce/gitlab.rb
  fi
}

function with-elk {
    check-env
    source .env
    docker network create skynet
    docker-compose  -f docker-compose.elk.yml \
                    -f docker-compose.yml \
    up -d
}

function with-elk-and-prometheus {
    check-env
    source .env
    docker network create skynet
    docker-compose  -f docker-compose.elk.yml \
                    -f docker-compose.prometheus.yml \
                    -f docker-compose.yml \
    up -d
}

function with-elk-and-selenium {
    check-env
    source .env
    docker network create skynet
    docker-compose  -f docker-compose.elk.yml \
                    -f docker-compose.selenium.yml \
                    -f docker-compose.yml \
    up -d
}

function full-options {
    check-env
    source .env
    docker network create skynet
    docker-compose  -f docker-compose.elk.yml \
                    -f docker-compose.mattermost.yml \
                    -f docker-compose.prometheus.yml \
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

'with-elk-and-prometheus')
with-elk-and-prometheus
;;

'with-elk-and-selenium')
with-elk-and-selenium
;;

'full-options')
full-options
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
echo "                    with-elk-and-prometheus"
echo "                    with-elk-and-selenium"
echo "                    full-options"
echo "                    reset"
echo ""
;;

esac
