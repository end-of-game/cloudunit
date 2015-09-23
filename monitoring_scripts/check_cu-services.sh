#!/bin/bash

# Author: Erik Kristensen
# Email: erik@erikkristensen.com
# License: MIT
# Nagios Usage: check_nrpe!check_docker_container!_container_id_
# Usage: ./check_docker_container.sh _container_id_
#
# The script checks if a container is running.
#   OK - running
#   WARNING - container is ghosted
#   CRITICAL - container is stopped
#   UNKNOWN - does not exist

for CONTAINER in java tomcat-6 tomcat-7 tomcat-8 jboss-7 jboss-8 jboss-5-1-0; do
    exit_code=$(docker inspect --format="{{ .State.ExitCode }}" $CONTAINER 2>/dev/null)

    if [ "$exit_code" != "0" ]; then
        echo "CRITICAL - Container $CONTAINER has not been launched."
        echo "$CONTAINER" > /tmp/service_cont_not_launched
        exit 2
    fi

done

echo "OK - All services containers have been launched."
