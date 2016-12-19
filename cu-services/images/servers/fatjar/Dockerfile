FROM openjdk:8

ENV TINI_VERSION v0.10.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

RUN mkdir -p /opt/cloudunit/fatjar &&  \
    mkdir -p /opt/cloudunit/fatjar/logs \
    mkdir -p /opt/cloudunit/backup &&  \
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

ENV CU_SERVER_RESTART_POST_DEPLOYMENT true
ENV CU_SOFTWARE /opt/cloudunit/fatjar
ENV CU_SERVER_MANAGER_PATH " "
ENV CU_SERVER_MANAGER_PORT " "
ENV CU_SERVER_PORT "8080"
ENV CU_SERVER_RESTART_POST_CREDENTIALS false
ENV CU_DEFAULT_LOG_FILE system.out
ENV CU_LOGS stdout
ENV JAVA_OPTS "-Dfile.encoding=UTF-8 -Xms512m -Xmx512m"
RUN apt-get update \
        && apt-get install -y --no-install-recommends netcat \
	    && rm -r /var/lib/apt/lists/*

RUN groupadd -r cloudunit --gid=999 && useradd -r -g cloudunit --uid=999 cloudunit
RUN chown -R cloudunit:cloudunit /opt/cloudunit
USER cloudunit

VOLUME /opt/cloudunit

ADD docker-entrypoint.sh /
COPY binary/boot.jar $CU_SOFTWARE/boot.jar

ENTRYPOINT ["/tini", "--", "/docker-entrypoint.sh"]

LABEL origin application
LABEL application-type fatjar
LABEL application-version undef

