#!/bin/bash

docker rm -vf $(docker ps -aq)
docker volumes rm -f $(docker volume ls -q)
docker rmi -f $(docker images -q)


