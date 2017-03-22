FROM openjdk:7

ARG WILDFLY_VERSION
ENV WILDFLY_VERSION ${WILDFLY_VERSION:-8.2.1.Final}

ARG WILDFLY_SHA1
ENV WILDFLY_SHA1 ${WILDFLY_SHA1:-77161d682005f26acb9d2df5548c8623ba3a4905}

# Set the WILDFLY_VERSION env variable
ENV WILDFLY_TGZ_URL https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz

ENV JBOSS_HOME /opt/cloudunit/wildfly

## CLOUDUNIT :: BEGINNING
# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/backup &&  \
    mkdir -p /opt/cloudunit/hooks/ && \
    mkdir -p /opt/cloudunit/java && \
    mkdir -p /opt/cloudunit/shared && \
    mkdir -p /opt/cloudunit/scripts && \
    mkdir -p /opt/cloudunit/tmp

WORKDIR /opt/cloudunit

# Environment variables
ENV CU_BACKUP /opt/cloudunit/backup
ENV CU_HOOKS /opt/cloudunit/hooks
ENV CU_JAVA /opt/cloudunit/java
ENV CU_SCRIPTS /opt/cloudunit/scripts
ENV CU_SHARED /opt/cloudunit/shared
ENV CU_TMP /opt/cloudunit/tmp

ENV CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV CU_SOFTWARE $JBOSS_HOME
ENV CU_SERVER_MANAGER_PATH " "
ENV CU_SERVER_MANAGER_PORT 9990
ENV CU_SERVER_PORT 8080
ENV CU_DEFAULT_LOG_FILE server.log
ENV CU_LOGS $JBOSS_HOME/standalone/log
ENV JAVA_OPTS "-Xms512m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true "
## CLOUDUNIT :: END

# Add the WildFly distribution to /opt/cloudunit/wildfly, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
RUN cd $HOME \
    && curl -O $WILDFLY_TGZ_URL \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && rm wildfly-$WILDFLY_VERSION.tar.gz

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

RUN groupadd -r cloudunit --gid=999 && useradd -r -g cloudunit --uid=999 cloudunit
RUN chown -R cloudunit:cloudunit /opt/cloudunit
USER cloudunit

VOLUME /opt/cloudunit/

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["run"]

LABEL origin cloudunit
LABEL application wildfly
LABEL version $WILDFLY_VERSION
