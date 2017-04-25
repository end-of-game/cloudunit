#!/bin/bash

CU_COMPOSE_FILES="-f docker-compose.elk.yml"

if [ "$1" = 'dev' ]; then
    CU_COMPOSE_FILES+=" -f docker-compose.dev.yml"
    exec docker-compose $CU_COMPOSE_FILES up -d
fi

if [ "$1" = 'production' ]; then
    CU_COMPOSE_FILES=" -f docker-compose.yml"
    exec docker-compose $CU_COMPOSE_FILES up -d
fi

exec "$@"