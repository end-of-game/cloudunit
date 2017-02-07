#!/bin/bash
set -e

# Run as user "kibana" if the command is "kibana"
if [ "$1" = 'kibana' ]; then
 	if [ "$ELASTICSEARCH_URL" != "http://:9200" ]; then
		sed -ri "s!^(\#\s*)?(elasticsearch\.url:).*!\2 '$ELASTICSEARCH_URL'!" /etc/kibana/kibana.yml
	else
		ELASTICSEARCH_URL=http://elasticsearch:9200
	fi
	set -- gosu kibana tini -- "$@"
fi
echo 'Waiting for elasticsearch Up and Running'
until $(curl --output /dev/null --silent --head --fail $ELASTICSEARCH_URL); do
  sleep 2s
done

sleep 1s

if $(curl --output /dev/null --silent --head --fail $ELASTICSEARCH_URL/.kibana)
then
  echo "Kibana index already exist"
else
	echo "Load Kibana index"
	elasticdump --input=file/kibana-index-mapping.json --output=$ELASTICSEARCH_URL/.kibana --type=mapping
	elasticdump --input=file/kibana-index.json --output=$ELASTICSEARCH_URL/.kibana --type=data
fi

# Add kibana as command if needed
if [[ "$1" == -* ]]; then
	set -- kibana "$@"
fi

exec "$@"
