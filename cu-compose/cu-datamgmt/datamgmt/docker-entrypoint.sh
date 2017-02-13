#!/bin/sh
set -e

echo 'Waiting for elasticsearch Up and Running'

if [ -z "$ELASTICSEARCH_URL" ]; then
  export ELASTICSEARCH_URL=http://elasticsearch:9200
fi

if [ -z "$LOGSTASH_URL" ]; then
  export LOGSTASH_URL=http://logstash:9600
fi

until $(curl --output /dev/null --silent --head --fail $ELASTICSEARCH_URL); do
  sleep 1s
done
until $(curl --output /dev/null --silent --head --fail $LOGSTASH_URL); do
  sleep 1s
done

/opt/datamgmt/manager/datamgmt
