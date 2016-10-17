#!/bin/bash

set -e

echo 'Waiting for Tomcat Up and Running'
until $(curl --output /dev/null --silent --head --fail http://cuplatform_tomcat_1:8080); do
  sleep 2s
done
sleep 1s

echo "Starting nginx..."
exec "$@" "-c /etc/nginx/nginx.conf -g daemon off;"

