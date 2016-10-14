#!/bin/bash

docker rm -vf $(docker ps -aq)
docker volume rm $(docker volume ls -q)
docker rmi -f $(docker images -q)

## ALWAYS RIGHT
exit 0

