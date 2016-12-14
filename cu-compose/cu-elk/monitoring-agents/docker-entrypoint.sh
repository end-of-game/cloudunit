#!/bin/bash

echo 'Waiting for elasticsearch Up and Running'
until $(curl --output /dev/null --silent --head --fail http://$ELASTICSEARCH_URL:9200); do
  sleep 2s
done

./metricbeat -e -v -c conf.d/default.yml
