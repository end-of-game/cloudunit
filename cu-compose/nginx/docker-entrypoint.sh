#!/bin/bash

set -e

function waitFor ( ) {
  echo "Waiting for" $1 "Up and Running"
  until (curl --output /dev/null --silent --head $2); do
    sleep 2s
  done
  echo "$1 is ready"
  sleep 1s
}

waitFor Tomcat  http://cuplatform_tomcat_1.manager.cloud.unit:8080
waitFor Jenkins http://cuplatform_jenkins_1.jenkins.cloud.unit:8080
waitFor Gitlab  http://cuplatform_gitlab_1.gitlab-ce.cloud.unit
waitFor Kibana  http://cuplatform_kibana_1.elk-kibana.cloud.unit:5601

echo "Nginx is started"
exec nginx -g "daemon off;"

