FROM mdelapenya/liferay-portal:7-ce-ga1-tomcat-hsql

MAINTAINER Nicolas Muller <n.muller@treeptik.fr>

ENV CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV CU_SOFTWARE $CATALINA_HOME
ENV CU_SERVER_MANAGER_PATH manager/html
ENV CU_SERVER_MANAGER_PORT 8080
ENV CU_SERVER_PORT 8080
ENV CU_LOGS /usr/local/liferay-portal-7.0-ce-ga1/logs
ENV CU_DEFAULT_LOG_FILE liferay.2017-06-19.log

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]
