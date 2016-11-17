FROM datadog/docker-dd-agent:latest

RUN apt-get update
RUN apt-get install -y openjdk-7-jdk 

ADD tomcat.yaml ./conf.d/tomcat.yaml
ADD mysql.yaml ./conf.d/mysql.yaml

RUN service datadog-agent stop
RUN service datadog-agent start
