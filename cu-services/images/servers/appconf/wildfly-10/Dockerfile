FROM cloudunit/base-14.04

# Set the WILDFLY_VERSION env variable
ENV WILDFLY_VERSION 10.0.0.Final
ENV WILDFLY_SHA1 c0dd7552c5207b0d116a9c25eb94d10b4f375549
ENV JBOSS_HOME /cloudunit/appconf/wildfly
ENV JAVA_HOME /cloudunit/java/jdk1.8.0_25

# Add the WildFly distribution to /cloudunit/appconf, and make wildfly the owner of the extracted tar content
# Make sure the distribution is available from a well-known place
RUN cd $HOME \
    && curl -O https://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.tar.gz \
    && sha1sum wildfly-$WILDFLY_VERSION.tar.gz | grep $WILDFLY_SHA1 \
    && tar xf wildfly-$WILDFLY_VERSION.tar.gz \
    && mv $HOME/wildfly-$WILDFLY_VERSION $JBOSS_HOME \
    && mv $JBOSS_HOME/standalone/configuration/standalone-full.xml $JBOSS_HOME/standalone/configuration/standalone.xml \
    && rm wildfly-$WILDFLY_VERSION.tar.gz

RUN cd /cloudunit/tmp && wget http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.39/mysql-connector-java-5.1.39.jar

#RUN cd $JBOSS_HOME/standalone/deployments && wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jolokia.war

# Ensure signals are forwarded to the JVM process correctly for graceful shutdown
ENV LAUNCH_JBOSS_IN_BACKGROUND true

# Expose the ports we're interested in
EXPOSE 8080
EXPOSE 22

# add custom scripts
ADD scripts /cloudunit/scripts
RUN chmod +x /cloudunit/scripts/*

RUN mkdir -p /cloudunit/appconf/logs/
RUN ln -s /cloudunit/appconf/wildfly/standalone/log/server.log /cloudunit/appconf/logs

VOLUME /cloudunit/appconf/logs
VOLUME /cloudunit/appconf/wildfly/standalone/log

ENTRYPOINT ["/cloudunit/scripts/start-service.sh"]


