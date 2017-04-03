FROM vertx/vertx3

MAINTAINER "Nicolas MULLER"

EXPOSE 8080

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

RUN mkdir -p /opt/cloudunit/verticles && \
    mkdir -p /opt/cloudunit/tmp

WORKDIR /opt/cloudunit

# Environment variables
ENV CU_SCRIPTS /opt/cloudunit/scripts
ENV CU_TMP /opt/cloudunit/tmp

ENV VERTICLE_HOME "/opt/cloudunit/verticles"

ENV CU_SERVER_RESTART_POST_DEPLOYMENT true
ENV CU_SOFTWARE $VERTICLE_HOME
ENV CU_SERVER_MANAGER_PATH " "
ENV CU_SERVER_MANAGER_PORT " "
ENV CU_SERVER_PORT "8080"
ENV CU_SERVER_RESTART_POST_CREDENTIALS false
ENV CU_DEFAULT_LOG_FILE system.out
ENV CU_LOGS stdout
ENV JAVA_OPTS "-Dfile.encoding=UTF-8 -Xms512m -Xmx512m"

COPY verticle.groovy /opt/cloudunit/verticles

# Launch the verticle
WORKDIR $VERTICLE_HOME
COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]

LABEL origin application
LABEL application-type vertx-groovy
LABEL application-version vertx-groovy-33


