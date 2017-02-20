# A automated logging manager for Cloudunit

datamgmt will manage file logging for docker container and send it all to a logstash grocker/parser.

Below logging Application are groked/parsed by logstash out of the box:
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
docker network create datamgmt
docker network create skynet
docker-compose -f docker-compose.datamgmt.yml up -d
```

Once the stack up and running let's execute a simple tomcat app with docker labels :
```
docker run -d --name=test-app -v /usr/local/tomcat/logs -l application-type=tomcat -l logging=enabled -l logging-type=file -l application-logs-path="/usr/local/tomcat/logs" tomcat:9-alpine

or with log driver to catch stdout container logs too

docker run -d --name=test-app -v /usr/local/tomcat/logs --log-driver=gelf --log-opt gelf-address=udp://localhost:12201 --log-opt tag=tomcat -l application-type=tomcat -l logging=enabled -l logging-type=file -l application-logs-path="/usr/local/tomcat/logs" tomcat:9-alpine
```

Let's check that logs are sent, connect to kibana web interface (http://localhost:5601/) and check documents in logstash index.

# How does it work (Advanced)

datamgmt-manager listen some specifics events on docker socket. These event are :
* container create with label logging=enabled and logging-type=file,
* container stop, die, kill with label logging=enabled and logging-type=file,
* container destroy with label logging=enabled and logging-type=file.

### Applicative container creation

1) If a container is created with label "application-type=xxx", "logging=enabled" and "logging-type=file" the event is catched

2) Once the event catched the manager create a filebeat container with a volumes from the applicative container with logs file inside. For security and permission concern, GID of the user in filebeat container is the same as the user in applicative one. The configuration file used by filebeat must have the name as the "application-type" label value, in order to use custom log file path, another label can be add "application-logs-path".

3) filebeat send logs content into logstash within filebeat protocol.

### Applicative container destroy

1) If a container is destroyed with label "application-type=xxx", "logging=enabled" and "logging-type=file" the event is catched

2) manager will delete filebeat container and all logs data stored in elasticsearch backend

### Applicative container stop, die, kill

1) If a container stopped, died or is killed with label "application-type=xxx", "logging=enabled" and "logging-type=file" the event is catched

2) manager will wait 2 seconds and if the applicative container still exist and is not running the filebeat container will be stopped without backend data deletion, otherwhise the container sill exist and running so nothing have to be done or the container doesn't exist anymore and do nothing because a destroy event will be catched by manager.




