FROM ubuntu:trusty
RUN apt-get update && apt-get install -y ssh

COPY loop.sh /
COPY tomcat.sh /

ENTRYPOINT ["/loop.sh"]

