#!/usr/bin/env bash

export CLOUDUNIT_HOME=/home/admincu/.jenkins/workspace/cloudunit
source /etc/environment
#cd cloudunit && git pull
cd $CLOUDUNIT_HOME/cu-services && echo yes | ./die-hard.sh
cd $CLOUDUNIT_HOME/cu-services && ./build-services.sh fatjar
docker network create skynet
cd $CLOUDUNIT_HOME/cu-compose && docker-compose -f docker-compose.elk.yml up -d
cd $CLOUDUNIT_HOME/integration-tests && docker-compose kill && docker-compose rm -f && docker-compose up -d
cd $CLOUDUNIT_HOME && /home/admincu/software/maven/bin/mvn clean install -DskipTests
cd $CLOUDUNIT_HOME/cu-manager && /home/admincu/software/maven/bin/mvn test "-Dtest=Tomcat8ApplicationControllerTestIT"

