FROM        java:8-jdk

ENV         JAVA_HOME         /usr/lib/jvm/java-8-openjdk-amd64
ENV         GLASSFISH_HOME    /usr/local/glassfish4
ENV         PATH              $PATH:$JAVA_HOME/bin:$GLASSFISH_HOME/bin

RUN         apt-get update && \
            apt-get install -y curl unzip zip inotify-tools netcat && \
            rm -rf /var/lib/apt/lists/*
RUN         curl -L -o /tmp/glassfish-4.1.zip http://download.java.net/glassfish/4.1/release/glassfish-4.1.zip && \
            unzip /tmp/glassfish-4.1.zip -d /usr/local && \
            rm -f /tmp/glassfish-4.1.zip

EXPOSE      8080 4848 8181
WORKDIR     /usr/local/glassfish4

## CLOUDUNIT :: START

# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/scripts && \
    mkdir -p /opt/cloudunit/tmp

# Environment variables
ENV CU_SCRIPTS /opt/cloudunit/scripts
ENV CU_TMP /usr/local/glassfish4

ENV         CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV         CU_SOFTWARE $GLASSFISH_HOME
ENV         CU_SERVER_MANAGER_PATH " "
ENV         CU_SERVER_MANAGER_PORT 4848
ENV         CU_SERVER_PORT 8080
ENV         CU_DEFAULT_LOG_FILE server.log
ENV         CU_LOGS $GLASSFISH_HOME/glassfish/domains/domain1/logs/

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]

LABEL origin application
LABEL application-type glassfish
LABEL application-version glassfish4
## CLOUDUNIT :: END

