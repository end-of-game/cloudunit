FROM cloudunit/base-14.04

ENV TINI_VERSION v0.10.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/fatjar &&  \
    mkdir -p /opt/cloudunit/fatjar/logs

## CLOUDUNIT :: BEGINNING
ENV CU_SERVER_RESTART_POST_DEPLOYMENT true
ENV CU_SOFTWARE /opt/cloudunit/fatjar
ENV CU_SERVER_MANAGER_PATH " "
ENV CU_SERVER_MANAGER_PORT " "
ENV CU_SERVER_PORT "8080"
ENV CU_SERVER_RESTART_POST_CREDENTIALS false
ENV CU_DEFAULT_LOG_FILE system.out
ENV CU_LOGS stdout
ENV CU_DEFAULT_JAVA_RELEASE "jdk1.8.0_25"
ENV JAVA_HOME /opt/cloudunit/java/$CU_DEFAULT_JAVA_RELEASE
ENV PATH $PATH:$JAVA_HOME/bin
ENV JAVA_OPTS "-Dfile.encoding=UTF-8 -Xms512m -Xmx512m"
## CLOUDUNIT :: END

RUN groupadd -r cloudunit --gid=999 && useradd -r -g cloudunit --uid=999 cloudunit
RUN chown -R cloudunit:cloudunit /opt/cloudunit
USER cloudunit

VOLUME /opt/cloudunit

ADD docker-entrypoint.sh /
COPY binary/boot.jar $CU_SOFTWARE/boot.jar

ENTRYPOINT ["/tini", "--", "/docker-entrypoint.sh"]



LABEL origin application
LABEL application-type fatjar
LABEL application-version ????
