# A automated logging manager for docker environment

datamgmt will manage file logging and send it all to a logstash grocker/parser.

Application supported:
* Tomcat(s)
* JBoss / WildFly 10 in progress
* FatJar for SpringBoot, Vertx, PlayFramework...

# How does it work

This agent will listen on docker event and if specific labels have been set on the container, it will grep logs file inside the container with a filebeat agent and send all logs to a logstash. Once the applicative container is deleted, logging container is deleted as well as the data stored in the elasticsearch backend.

# Quickstart

In order to use filebeat module we have to build the image :
```
docker build -t cloudunit/datamgmt-filebeat datamgmt/tools/filebeat/
```

In order handle logs, we need to start somme tools. All the stack could be loaded with a compose file :
```
docker network create datagmgt
docker-compose -f docker-compose.datamgmt.yml up -d
```

Once the stack up and running let's execute a simple tomcat app with docker labels :
```
docker run -d --name test-app -l application-type tomcat -l logging enabled -l logging-type file -l application-logs-path /usr/local/tomcat/logs tomcat:9-alpine
```

Let's check that logs are sent, connect to kibana web interface and check logs:
[[https://github.com/username/repository/blob/master/img/octocat.png|alt=octocat]]
