#!/bin/bash

docker run --name java cloudunit/java 
docker run --name tomcat-6 cloudunit/tomcat-6 
docker run --name tomcat-7 cloudunit/tomcat-7
docker run --name tomcat-8 cloudunit/tomcat-8
docker run --name jboss-7 cloudunit/jboss-7
docker run --name jboss-8 cloudunit/jboss-8
docker run --name jboss-5-1-0 cloudunit/jboss-5-1-0
