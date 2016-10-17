FROM cloudunit/base-14.04

MAINTAINER s.musso@treeptik.fr

########################
## CLOUDUNIT SPECIFIC ##
########################

RUN cd /opt/cloudunit/ \
    && wget https://github.com/Treeptik/cloudunit/releases/download/1.0/beats-agents.tar.gz \
    && tar zxvf beats-agents.tar.gz \
    && rm beats-agents.tar.gz \
    && rm -rf beats-agents/jmxproxybeat

WORKDIR /opt/cloudunit/beats-agents/dockbeat

ENTRYPOINT ["./dockbeat"]

LABEL origin cloudunit-monitoring
LABEL application-type dockbeat-polling
LABEL application-version 1.0
