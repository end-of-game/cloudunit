#!/usr/bin/env bash

if [ "$USER" == "vagrant" ];
then
    echo "thell to execute outside the vagrant box. Please exit"
    exit 1
fi

mvn clean install -DskipTests
cd cu-manager/src/main/webapp
grunt serve
