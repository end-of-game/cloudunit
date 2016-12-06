#!/bin/sh
set -e

# first arg is `-f` or `--some-option`
# or first arg is `something.conf`
if [ "${1#-}" != "$1" ] || [ "${1%.conf}" != "$1" ]; then
	set -- redis-server "$@"
fi

# allow the container to be started with `--user`
if [ "$1" = 'redis-server' -a "$(id -u)" = '0' ]; then
	chown -R redis .
	exec gosu redis "$0" "$@"
fi

if [ -z "$APPLICATIVE_MONITORING" ] || [ "$APPLICATIVE_MONITORING" -eq 1 ]; then
	nohup /opt/cloudunit/polling-agents/metricbeat/metricbeat -c /opt/cloudunit/polling-agents/metricbeat/metricbeat.yml > /dev/null 2>&1 &
fi

exec "$@"
