FROM openjdk:8

MAINTAINER Nicolas Muller <n.muller@treeptik.fr>

ENV CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV CU_SOFTWARE $CATALINA_HOME
ENV CU_SERVER_MANAGER_PATH manager/html
ENV CU_SERVER_MANAGER_PORT 8080
ENV CU_SERVER_PORT 8080
ENV CU_LOGS /usr/local/liferay-portal-7.0-ce-ga1/logs
ENV CU_DEFAULT_LOG_FILE liferay.YYYY-MM-DD.log
ENV CATALINA_HOME /opt/liferay-ee-7.0-sp3/tomcat-8.0.32

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

COPY files/liferay-dxp-digital-enterprise-tomcat-7.0-sp3.zip /
RUN unzip /liferay-dxp-digital-enterprise-tomcat-7.0-sp3.zip && \
    mv /liferay-dxp-digital-enterprise-7.0-sp3/ /opt/liferay-ee-7.0-sp3 && \
    rm -f /liferay-dxp-digital-enterprise-tomcat-7.0-sp3.zip

RUN mkdir -p /opt/liferay-ee-7.0-sp3/data/deploy
COPY files/activation-ee.xml /opt/liferay-ee-7.0-sp3/data/deploy/activation-ee.xml

EXPOSE 8080

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]
