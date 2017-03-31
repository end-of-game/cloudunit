FROM tomcat:8.0.42-jre8

ARG user=tomcat
ARG group=tomcat
ARG uid=1000
ARG gid=1000

ENV JAVA_OPTS "-Dfile.encoding=UTF-8 -Xms512m -Xmx512m -XX:MaxPermSize=256m -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

RUN wget -O lib/javamelody-1.61.0.jar https://github.com/Treeptik/cloudunit/releases/download/1.0/javamelody-1.61.0.jar
RUN wget -O lib/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar https://github.com/Treeptik/cloudunit/releases/download/1.0/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar
COPY files/web.xml conf/
COPY files/logging.properties conf/
COPY files/jmxtrans-agent.xml conf/

RUN groupadd -g ${gid} ${group} \
    && useradd -d "$CATALINA_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}
RUN chown -R ${user}:${group} $CATALINA_HOME

VOLUME $CATALINA_HOME/logs

COPY docker-entrypoint.sh bin/
USER ${user}
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["catalina.sh","run"]

LABEL io.cloudunit="" \
      io.cloudunit.type=server \
      io.cloudunit.name.service=tomcat \
      io.cloudunit.name.display="Tomcat $TOMCAT_MAJOR" \
      io.cloudunit.version=$TOMCAT_MAJOR \
      io.cloudunit.variables="{\"TOMCAT_USER\": \"USER\", \"TOMCAT_PASSWORD\": \"PASSWORD\"}" \
      io.cloudunit.logfile=$CATALINA_HOME/logs/catalina.log \
      io.cloudunit.manager.path=manager/html \
      io.cloudunit.manager.port=8080 \
      io.cloudunit.tmp=$CATALINA_HOME/temp

