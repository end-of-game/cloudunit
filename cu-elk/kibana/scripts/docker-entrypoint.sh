#!/bin/bash
set -e

ELASTICSEARCH_URL=192.168.50.4

# Add kibana as command if needed
if [[ "$1" == -* ]]; then
	set -- kibana "$@"
fi

# Run as user "kibana" if the command is "kibana"
if [ "$1" = 'kibana' ]; then
	if [ "$ELASTICSEARCH_URL" ]; then
		sed -ri "s!^(\#\s*)?(elasticsearch\.url:).*!\2 '$ELASTICSEARCH_URL'!" /etc/kibana/kibana.yml
	fi

	set -- gosu kibana tini -- "$@"
fi

echo 'Waiting for elasticsearch Up and Running'
until $(curl --output /dev/null --silent --head --fail http://cu-elasticsearch:9200); do
  sleep 2s
done


echo 'Elasticsearch Up and Running, Check data'

cd /opt/cloudunit/scripts
node es-init.js&

exec "$@"
