#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, download latest images for all major versions"
    docker build --rm --no-cache -t cloudunit/wildfly-8 --build-arg WILDFLY_VERSION=8.2.1.Final --build-arg WILDFLY_SHA1=77161d682005f26acb9d2df5548c8623ba3a4905 images/servers/wildfly-8
    docker build --rm --no-cache -t cloudunit/wildfly-9 --build-arg WILDFLY_VERSION=9.0.2.Final --build-arg WILDFLY_SHA1=75738379f726c865d41e544e9b61f7b27d2853c7 images/servers/wildfly-9
    docker build --rm --no-cache -t cloudunit/wildfly-10 --build-arg WILDFLY_VERSION=10.1.0.Final --build-arg WILDFLY_SHA1=9ee3c0255e2e6007d502223916cefad2a1a5e333 images/servers/wildfly-10
  else
    if [ $# -eq 1 ] || [ $1 -eq "-h" ]
      then
      echo "Not enough argument must add version and sha1 key"
      echo "Usage exemple : ./wildfly 10.1.0.Final 77161d682005f26acb9d2df5548c8623ba3a4905"
      echo " Get SHA1 on https://hub.docker.com/r/jboss/wildfly/"
    elif [ $# -eq 2 ]
      then
      WILDFLY_VERSION=$1
      WILDFLY_SHA1=$2
      echo "Download version $WILDFLY_VERSION of wildfly"
      WILDFLY_MAJOR_VERSION=`echo $WILDFLY_VERSION | cut -d \. -f 1`
        docker build --rm --no-cache -t cloudunit/wildfly-$WILDFLY_MAJOR_VERSION --build-arg WILDFLY_VERSION=$WILDFLY_VERSION --build-arg WILDFLY_SHA1=$WILDFLY_SHA1 images/servers/wildfly-$WILDFLY_MAJOR_VERSION
    else
        echo "Error, Too much argument, type ./wildfly.sh -h for help"
    fi
fi