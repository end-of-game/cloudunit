#!/usr/bin/env bash

source /etc/environment
docker rm -f $(docker ps -aq)
docker run --name cu-monitoring-agents -d cloudunit/elk-monitoring-agents
cd ~/cloudunit && git pull
cd ~/cloudunit/cu-services && echo yes | ./die-hard.sh
cd ~/cloudunit/cu-services && ./build-services.sh tomcat
cd ~/cloudunit/cu-compose && docker-compose -f docker-compose.elk.yml up -d
cd ~/cloudunit/integration-tests && docker-compose kill && docker-compose rm -f && docker-compose up -d
cd ~/cloudunit && mvn clean install -DskipTests
cd ~/cloudunit/cu-manager && mvn test "-Dtest=*IT"

