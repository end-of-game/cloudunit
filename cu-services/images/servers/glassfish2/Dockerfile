FROM java:8

ENV GLASSFISH_HOME /opt/glassfish
ENV PATH $PATH:$GLASSFISH_HOME/bin
WORKDIR /opt/glassfish

RUN \
  apt-get update && \
  apt-get -yq install wget && \
  cd /opt && \
  wget -O glassfish.jar http://dlc-cdn.sun.com/javaee5/v2.1.1_branch/promoted/Linux/glassfish-installer-v2.1.1-b31g-linux.jar && \
  echo A|java -jar glassfish.jar && \
  rm glassfish.jar && \
  cd /opt/glassfish && \
  chmod -R +x lib/ant/bin

COPY setup.xml setup.xml

RUN lib/ant/bin/ant -f setup.xml && \
    chmod a+x bin/asadmin

## CLOUDUNIT :: START

# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/scripts && \
    mkdir -p /opt/cloudunit/tmp

# Environment variables
ENV CU_SCRIPTS /opt/cloudunit/scripts
ENV CU_TMP /opt/glassfish

ENV         CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV         CU_SOFTWARE $GLASSFISH_HOME
ENV         CU_SERVER_MANAGER_PATH " "
ENV         CU_SERVER_MANAGER_PORT 4848
ENV         CU_SERVER_PORT 8080
ENV         CU_DEFAULT_LOG_FILE server.log
ENV         CU_LOGS $GLASSFISH_HOME/domains/domain1/logs/

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]

LABEL origin application
LABEL application-type glassfish
LABEL application-version glassfish2
## CLOUDUNIT :: END

