#!/usr/bin/env bash

source /etc/environment
#cd ~/cloudunit && git pull
#cd ~/cloudunit/cu-services && echo yes | ./die-hard.sh
#cd ~/cloudunit/cu-services && ./build-services.sh fatjar
docker network create skynet
cd ~/cloudunit/cu-compose && docker-compose -f docker-compose.elk.yml up -d
cd ~/cloudunit/integration-tests && docker-compose kill && docker-compose rm -f && docker-compose up -d
cd ~/cloudunit && /home/admincu/software/maven/bin/mvn clean install -DskipTests
cd ~/cloudunit/cu-manager && /home/admincu/software/maven/bin/mvn test "-Dtest=Tomcat8ApplicationControllerTestIT"

