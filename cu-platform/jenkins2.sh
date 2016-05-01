#!/usr/bin/env bash

docker run  --name jenkins2 \
            -d -p 9080:8080 -p 50000:50000 \
            -v /home/vagrant/docker:/var/jenkins_home \
            jenkinsci/jenkins:2.0


