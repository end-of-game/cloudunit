#!/usr/bin/env bash

docker run  --name jenkins2 \
            -d -p 9080:8080 -p 50000:50000 \
            -v /var/jenkins_home \
            jenkinsci/jenkins:2.0-rc-1

## --env JAVA_OPTS=-Dhudson.footerURL=http://mycompany.com \

