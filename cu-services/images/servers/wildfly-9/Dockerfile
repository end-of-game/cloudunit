FROM cloudunit/base-14.04

# Set the WILDFLY_VERSION env variable
ARG WILDFLY_VERSION
ENV WILDFLY_TGZ_URL https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz
ARG WILDFLY_SHA1
ENV JBOSS_HOME /opt/cloudunit/wildfly

## CLOUDUNIT :: BEGINNING
ENV CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV CU_SOFTWARE $JBOSS_HOME
ENV CU_SERVER_MANAGER_PATH " "
ENV CU_SERVER_MANAGER_PORT 9990
ENV CU_SERVER_PORT 8080
ENV CU_SERVER_RESTART_POST_CREDENTIALS false
ENV CU_DEFAULT_LOG_FILE server.log
ENV CU_LOGS $JBOSS_HOME/standalone/log
ENV CU_DEFAULT_JAVA_RELEASE "jdk1.7.0_55"
ENV JAVA_HOME /opt/cloudunit/java/$CU_DEFAULT_JAVA_RELEASE
ENV JAVA_OPTS "-Xms512m -Xmx512m -XX:MaxPermSize=256m -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=$JBOSS_MODULES_SYSTEM_PKGS -Djava.awt.headless=true  -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
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

CMD ["/opt/cloudunit/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

LABEL origin application
LABEL application-type wildfy
LABEL application-version $WILDFLY_VERSION
