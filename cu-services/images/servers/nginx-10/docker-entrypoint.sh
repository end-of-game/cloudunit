#!/bin/bash

# Fix permission ownership
chown -R www-data:www-data /usr/share/nginx/html

if [ -z "$APPLICATIVE_MONITORING" ] || [ "$APPLICATIVE_MONITORING" -eq 1 ]; then
	/opt/cloudunit/monitoring-agents/metricbeat/metricbeat -c /opt/cloudunit/monitoring-agents/metricbeat/conf.d/nginx.yml&
fi

if [[ $1 == "nginx" ]]; then
  exec nginx -g "daemon off;"
else
  exec $1
fi

exec "$@"
