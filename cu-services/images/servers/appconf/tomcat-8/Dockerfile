FROM cloudunit/base-14.04

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

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

# Create directory for CloudUnit
RUN mkdir -p /opt/cloudunit/tomcat &&  \
    mkdir -p /opt/cloudunit/tomcat/logs

ENV CATALINA_HOME /opt/cloudunit/tomcat
ENV CU_SOFTWARE $CATALINA_HOME
ENV CU_LOGS $CATALINA_HOME/logs

RUN echo "CU_SOFTWARE=$CU_SOFTWARE" >> /etc/environment && \
    echo "CU_LOGS=$CU_LOGS" >> /etc/environment && \
    echo "CATALINA_HOME=$CATALINA_HOME" >> /etc/environment

ENV JAVA_HOME /opt/cloudunit/java/jdk1.8.0_25
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p $CATALINA_HOME

ENV TOMCAT_MAJOR 8
ENV TOMCAT_VERSION 8.0.36
ENV TOMCAT_TGZ_URL https://www.apache.org/dist/tomcat/tomcat-$TOMCAT_MAJOR/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

RUN cd $HOME \
    && wget $TOMCAT_TGZ_URL \
    && tar xvf apache-tomcat-$TOMCAT_VERSION.tar.gz  \
    && mv $HOME/apache-tomcat-$TOMCAT_VERSION/* $CATALINA_HOME/ \
    && rm apache-tomcat-$TOMCAT_VERSION.tar.gz

RUN groupadd -r cloudunit --gid=999 && useradd -r -g cloudunit --uid=999 cloudunit
RUN chown -R cloudunit:cloudunit /opt/cloudunit
USER cloudunit

VOLUME /opt/cloudunit/tomcat/logs

ENTRYPOINT ["/opt/cloudunit/scripts/start-service.sh"]

