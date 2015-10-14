# TOMCAT-6.0.41
FROM cloudunit/tomcat-base

# AJOUT DE LA VERSION DE TOMCAT
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/apache-tomcat-6.0.41.tar.gz -O /tmp/apache-tomcat-6.0.41.tar.gz \
	&& tar -xvf /tmp/apache-tomcat-6.0.41.tar.gz -C /cloudunit/binaries && rm /tmp/apache-tomcat-6.0.41.tar.gz

VOLUME /cloudunit/binaries
VOLUME /cloudunit/scripts

