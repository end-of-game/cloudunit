FROM vertx/vertx3:3.3.3

MAINTAINER "Nicolas MULLER"

EXPOSE 8080

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

RUN mkdir -p /opt/cloudunit/verticles && \
    mkdir -p /opt/cloudunit/tmp

WORKDIR /opt/cloudunit

# Environment variables
ENV VERTICLE_HOME "/opt/cloudunit/verticles"
ENV JAVA_OPTS "-Dfile.encoding=UTF-8 -Xms512m -Xmx512m"
ENV VERTX_MAJOR "3.3"

COPY verticle.groovy /opt/cloudunit/verticles

# Launch the verticle
WORKDIR $VERTICLE_HOME
COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]

LABEL io.cloudunit="" \
      io.cloudunit.type=server \
      io.cloudunit.name.service=vertx \
      io.cloudunit.name.display="Vert.x $VERTX_MAJOR" \
      io.cloudunit.version=$VERTX_MAJOR
