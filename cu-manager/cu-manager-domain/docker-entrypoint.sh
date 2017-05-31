#!/bin/bash

if [ "$1" = 'start' ]; then
    cd cu-manager/cu-manager-domain/target
    exec java -Dspring.profiles.active=local -jar cu-manager-domain-2.0.0-SNAPSHOT-boot.jar
fi

exec "$@"