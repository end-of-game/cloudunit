#!/bin/bash
set -e

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
until $(curl --output /dev/null --silent --head --fail http://elasticsearch:9200); do
  sleep 2s
done
  sleep 1s

if $(curl --output /dev/null --silent --head --fail http://elasticsearch:9200/.kibana)
then
  echo "Kibana index already exist"
else
	echo "Load Kibana index"
	elasticdump --input=file/kibana-index-mapping.json --output=http://elasticsearch:9200/.kibana --type=mapping
	elasticdump --input=file/kibana-index.json --output=http://elasticsearch:9200/.kibana --type=data
fi

exec "$@"