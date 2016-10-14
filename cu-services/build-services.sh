#!/bin/bash

docker build --rm --no-cache -t cloudunit/base-14.04 images/base-jessie
docker build --rm --no-cache -t cloudunit/base-14.04 images/base-12.04
docker build --rm --no-cache -t cloudunit/base-14.04 images/base-14.04
docker build --rm --no-cache -t cloudunit/base-14.04 images/base-16.04

docker build --rm --no-cache -t cloudunit/apache-2-2 images/servers/apache-2-2
docker build --rm --no-cache -t cloudunit/fatjar images/servers/fatjar
docker build --rm --no-cache -t cloudunit/tomcat-6 --build-arg TOMCAT_VERSION=6.0.45 images/servers/tomcat-6
docker build --rm --no-cache -t cloudunit/tomcat-7 --build-arg TOMCAT_VERSION=7.0.70 images/servers/tomcat-7
docker build --rm --no-cache -t cloudunit/tomcat-8 --build-arg TOMCAT_VERSION=8.0.37 images/servers/tomcat-8
docker build --rm --no-cache -t cloudunit/tomcat-85 --build-arg TOMCAT_VERSION=8.5.5 images/servers/tomcat-85
docker build --rm --no-cache -t cloudunit/tomcat-9 --build-arg TOMCAT_VERSION=9.0.0.M10 images/servers/tomcat-9
docker build --rm --no-cache -t cloudunit/wildfly-8 --build-arg WILDFLY_VERSION=8.2.1.Final --build-arg WILDFLY_SHA1=77161d682005f26acb9d2df5548c8623ba3a4905 images/servers/wildfly-8
docker build --rm --no-cache -t cloudunit/wildfly-9 --build-arg WILDFLY_VERSION=9.0.2.Final --build-arg WILDFLY_SHA1=75738379f726c865d41e544e9b61f7b27d2853c7 images/servers/wildfly-9
docker build --rm --no-cache -t cloudunit/wildfly-10 --build-arg WILDFLY_VERSION=10.1.0.Final --build-arg WILDFLY_SHA1=9ee3c0255e2e6007d502223916cefad2a1a5e333 images/servers/wildfly-10

docker build --rm --no-cache -t cloudunit/postgresql-9-3 images/modules/postgresql-9-3
docker build --rm --no-cache -t cloudunit/postgresql-9-4 images/modules/postgresql-9-4
docker build --rm --no-cache -t cloudunit/postgresql-9-5 images/modules/postgresql-9-5
docker build --rm --no-cache -t cloudunit/postgis-2-2 images/modules/postgis-2-2
docker build --rm --no-cache -t cloudunit/mysql-5-5 images/modules/mysql-5-5
docker build --rm --no-cache -t cloudunit/mysql-5-6 images/modules/mysql-5-6
docker build --rm --no-cache -t cloudunit/mysql-5-7 images/modules/mysql-5-7

docker build --rm --no-cache -t cloudunit/activemq-5.13.2 images/modules/activemq-5.13.2
docker build --rm --no-cache -t cloudunit/rabbitmq-3.6.5-1 images/modules/rabbitmq-3.6.5-1

docker build --rm --no-cache -t cloudunit/elasticsearch-2.4 images/modules/elasticsearch-2.4
docker build --rm --no-cache -t cloudunit/redis-3-2 images/modules/redis-3-2

docker build --rm --no-cache -t cloudunit/fatjar images/tools/java

./check_build_images.sh


