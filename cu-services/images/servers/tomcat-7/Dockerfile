FROM openjdk:8-jdk

RUN set -ex \
	&& for key in \
		05AB33110949707C93A279E3D3EFE6B686867BA6 \
		07E48665A34DCAFAE522E5E6266191C37C037D42 \
		47309207D818FFD8DCD3F83F1931D684307A10A5 \
		541FBE7D8F78B25E055DDEE13C370389288584E7 \
		61B832AC2F1C5A90F0F9B00A1C506407564C17A3 \
		79F7026C690BAA50B92CD8B66A3AD3F4F22C4FED \
		80FF76D88A969FE46108558A80B953A041E49465 \
		8B39757B1D8A994DF2433ED58B3A601F08C975E5 \
		A27677289986DB50844682F8ACB77FC2E86E29AC \
		A9C5DF4D22E99998D9875A5110C01C5A2F6059E7 \
		B3F49CD3B9BD2996DA90F817ED3873F5D3262722 \
		DCFD35E0BF8CA7344752DE8B6FB21E8933C60243 \
		F3A04C595DB5B6A5F1ECA43E3B7BBB100D811BBE \
		F7DA48BB64BCB84ECBA7EE6935CD23C10D498E23 \
	; do \
		gpg --keyserver ha.pool.sks-keyservers.net --recv-keys "$key"; \
	done

ARG TOMCAT_VERSION
ENV TOMCAT_VERSION ${TOMCAT_VERSION:-7.0.70}

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/tomcat &&  \
    mkdir -p /opt/cloudunit/tomcat/logs

ENV CATALINA_HOME /opt/cloudunit/tomcat
ENV TOMCAT_MAJOR 7
ARG TOMCAT_VERSION
ENV TOMCAT_TGZ_URL https://archive.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz
ENV PATH $CATALINA_HOME/bin:$PATH

## CLOUDUNIT :: BEGINNING
# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/backup &&  \
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

ENV CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV CU_SOFTWARE $CATALINA_HOME
ENV CU_SERVER_MANAGER_PATH manager/html
ENV CU_SERVER_MANAGER_PORT 8080
ENV CU_SERVER_PORT 8080
ENV CU_DEFAULT_LOG_FILE catalina.log
ENV CU_LOGS $CATALINA_HOME/logs
ENV JAVA_OPTS "-Dfile.encoding=UTF-8 -Xms512m -Xmx512m -XX:MaxPermSize=256m -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
## CLOUDUNIT :: END

RUN mkdir -p $CATALINA_HOME

RUN cd $HOME \
    && wget $TOMCAT_TGZ_URL \
    && tar xvf apache-tomcat-$TOMCAT_VERSION.tar.gz  \
    && mv $HOME/apache-tomcat-$TOMCAT_VERSION/* $CATALINA_HOME/ \
    && rm apache-tomcat-$TOMCAT_VERSION.tar.gz \
    && rm -rf $CU_SOFTWARE/webapps/ROOT $CU_SOFTWARE/webapps/examples $CU_SOFTWARE/webapps/docs

## CLOUDUNIT :: BEGINNING
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/javamelody-1.61.0.jar \
    && mv javamelody-1.61.0.jar $CU_SOFTWARE/lib/javamelody-1.61.0.jar
COPY files/web.xml $CU_SOFTWARE/conf/web.xml
COPY files/tomcat-users.xml $CU_SOFTWARE/conf/tomcat-users.xml
COPY files/logging.properties $CU_SOFTWARE/conf/logging.properties
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar \
    && mv jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar $CU_SOFTWARE/lib/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar
COPY files/jmxtrans-agent.xml /opt/cloudunit/conf/jmxtrans-agent.xml
ENV JAVA_OPTS="$JAVA_OPTS -javaagent:/opt/cloudunit/tomcat/lib/jmxtrans-agent-1.2.5-SNAPSHOT-jar-with-dependencies.jar=/opt/cloudunit/conf/jmxtrans-agent.xml"

RUN groupadd -r cloudunit --gid=999 && useradd -r -g cloudunit --uid=999 cloudunit
RUN chown -R cloudunit:cloudunit /opt/cloudunit
USER cloudunit

VOLUME /opt/cloudunit

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]
CMD [ "run" ]

LABEL origin application
LABEL application-type tomcat
LABEL application-version tomcat-$TOMCAT_VERSION
## CLOUDUNIT :: END
