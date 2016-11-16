#!/usr/bin/env bash
docker-compose  --file docker-compose.dev.yml \
                --file docker-compose.elk.yml \
                --file docker-compose.test.yml \
                ps 

