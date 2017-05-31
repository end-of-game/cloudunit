#!/bin/bash

if [ "$1" = 'start' ]; then
    cd cu-manager/cu-docker-orchestrator/target
    exec java -Dspring.profiles.active=local -jar cu-docker-orchestrator-2.0.0-SNAPSHOT-boot.jar
fi

exec "$@"