FROM cloudunit/base-14.04

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/fatjar &&  \
    mkdir -p /opt/cloudunit/fatjar/logs && \
    mkdir 

## CLOUDUNIT :: BEGINNING
ENV CU_SOFTWARE /opt/cloudunit/fatjar
ENV CU_SERVER_MANAGER_PATH " "
ENV CU_SERVER_MANAGER_PORT " "
ENV CU_SERVER_PORT " "
ENV CU_SERVER_RESTART_POST_CREDENTIALS false
ENV CU_DEFAULT_LOG_FILE " "
ENV CU_LOGS $CU_SOFTWARE/logs
ENV CU_DEFAULT_JAVA_RELEASE "jdk1.8.0_25"
ENV JAVA_HOME /opt/cloudunit/java/$CU_DEFAULT_JAVA_RELEASE
ENV JAVA_OPTS "-Dfile.encoding=UTF-8 -Xms512m -Xmx512m -XX:MaxPermSize=256m"
## CLOUDUNIT :: END

RUN groupadd -r cloudunit --gid=999 && useradd -r -g cloudunit --uid=999 cloudunit
RUN chown -R cloudunit:cloudunit /opt/cloudunit
USER cloudunit

VOLUME /opt/cloudunit

ENTRYPOINT ["/cloudunit/scripts/start.sh"]

