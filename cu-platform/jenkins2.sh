#!/usr/bin/env bash

docker run --name jenkins2 -p 8080:8080 -p 50000:50000 -v ~/docker/jenkins:/etc/jenkins_home jenkinsci/jenkins:2.0-alpha-3
