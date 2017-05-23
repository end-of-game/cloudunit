FROM jboss/wildfly:10.1.0.Final

ENV WILDFLY_MAJOR 10

ENV PATH $JBOSS_HOME/bin:$PATH
WORKDIR $JBOSS_HOME

ENV JAVA_OPTS "-Xms512m -Xmx512m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true "

RUN mkdir $JBOSS_HOME/standalone/log
RUN mkdir $JBOSS_HOME/temp

VOLUME $JBOSS_HOME/standalone/log

COPY scripts/deploy.sh bin/

COPY docker-entrypoint.sh bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["standalone.sh","-b","0.0.0.0","-bmanagement","0.0.0.0"]

LABEL io.cloudunit="" \
      io.cloudunit.type=server \
      io.cloudunit.name.service=wildfly \
      io.cloudunit.name.display="Wildfly $WILDFLY_MAJOR" \
      io.cloudunit.version=$WILDFLY_MAJOR \
      io.cloudunit.variables="{\"WILDFLY_USER\": \"USER\", \"WILDFLY_PASSWORD\": \"PASSWORD\"}" \
      io.cloudunit.logfile=$JBOSS_HOME/standalone/log/server.log \
      io.cloudunit.manager.path="" \
      io.cloudunit.manager.port=9990 \
      io.cloudunit.tmp=$JBOSS_HOME/temp \
      io.cloudunit.deploy="deploy.sh {0} {1}"

