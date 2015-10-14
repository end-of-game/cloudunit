# Base Dockerfile for all SERVER IMAGES
FROM cloudunit/base-12.04

# INSTALLATION PRODUITS ANNEXES (GIT, ...)
RUN apt-get install -y git expect apache2 apache2-utils rsyslog
RUN sed -i 's|/var/log/auth.log|/var/log/cloudunit/auth.log|' /etc/rsyslog.d/50-default.conf

# INSTALLATION MAVEN 3.0.5
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/apache-maven-3.0.5-bin.tar.gz -O /tmp/apache-maven-3.0.5-bin.tar.gz \
	&& tar -xvf /tmp/apache-maven-3.0.5-bin.tar.gz -C /cloudunit/software && rm /tmp/apache-maven-3.0.5-bin.tar.gz
ENV MAVEN_HOME /cloudunit/software/apache-maven-3.0.5
ENV PATH /cloudunit/software/apache-maven-3.0.5/bin:$PATH
RUN ln -s /cloudunit/software/apache-maven-3.0.5/bin/mvn /bin/mvn

# INSTALLATION JAVA ORACLE
ENV JAVA_HOME /cloudunit/java/jdk1.7.0_55

# Par défaut java 7 est utilisé
ENV PATH $JAVA_HOME/bin:$PATH
RUN echo -n "JAVA_HOME=$JAVA_HOME\n" >> /etc/environment

# VARIABLES ENVIRONNEMENT GIT
RUN mkdir /cloudunit/git /cloudunit/config
ENV CU_GIT_HOME /cloudunit/git
ENV CU_USER_CONFIG /cloudunit/config
RUN echo -n "CU_GIT_HOME=$CU_GIT_HOME\nCU_USER_CONFIG=$CU_USER_CONFIG\n" >> /etc/environment

# Ajout des scripts utile aux managers et au démarrage container
ADD scripts /cloudunit/scripts/
RUN chmod -R +x /cloudunit/scripts

EXPOSE 22
