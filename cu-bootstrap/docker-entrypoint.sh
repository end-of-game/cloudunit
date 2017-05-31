#!/bin/bash

CU_COMPOSE_FILES="-f /app/docker-compose.yml"

if [ "$1" = 'start' ]; then
    exec docker-compose $CU_COMPOSE_FILES up -d
fi

exec "$@"