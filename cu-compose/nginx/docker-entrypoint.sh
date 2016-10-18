#!/bin/bash

set -e

function waitFor ( ) {
  echo "Waiting for" $1 "Up and Running"
  until $(curl --output /dev/null --silent --head --fail $2); do
    sleep 2s
  done
  sleep 1s
}

waitFor Tomcat  http://cuplatform_tomcat_1.manager.cloud.unit:8080
waitFor Jenkins http://cuplatform_jenkins.jenkins.cloud.unit:8080
waitFor Gitlab  http://cuplatform_gitlab_1.gitlab-ce.cloud.unit

echo "Starting nginx..."
exec nginx -g "daemon off;"

