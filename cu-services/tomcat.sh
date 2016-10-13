#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "No arguments supplied, download latest images for all major versions"
    docker build --rm --no-cache -t cloudunit/base-14.04 images/base-14.04
    docker build --rm --no-cache -t cloudunit/tomcat-6 --build-arg TOMCAT_VERSION=6.0.45 images/servers/tomcat-6
    docker build --rm --no-cache -t cloudunit/tomcat-7 --build-arg TOMCAT_VERSION=7.0.70 images/servers/tomcat-7
    docker build --rm --no-cache -t cloudunit/tomcat-8 --build-arg TOMCAT_VERSION=8.0.37 images/servers/tomcat-8
    docker build --rm --no-cache -t cloudunit/tomcat-85 --build-arg TOMCAT_VERSION=8.5.5 images/servers/tomcat-85
    docker build --rm --no-cache -t cloudunit/tomcat-9 --build-arg TOMCAT_VERSION=9.0.0.M10 images/servers/tomcat-9
  else
    if [ $# -eq 1 ]
      then
      if [[ ${1:0:2} == "8.5" ]]
        then 
          docker build --rm --no-cache -t cloudunit/tomcat-85 --build-arg TOMCAT_VERSION=$1 images/servers/tomcat-85
        else
        docker build --rm --no-cache -t cloudunit/tomcat-${1:0:1} --build-arg TOMCAT_VERSION=$1 images/servers/tomcat-${1:0:1}
      fi
      else
        echo "Too much argument"
    fi
fi

