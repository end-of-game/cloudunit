FROM cloudunit/jboss-base

#
# Installation de Jboss
#
RUN wget https://github.com/Treeptik/cloudunit/releases/download/0.9/jboss5.1.0.GAbin.tar.gz -O /tmp/jboss5.1.0.GAbin.tar.gz \
	&& tar -xvf /tmp/jboss5.1.0.GAbin.tar.gz -C /cloudunit/binaries && rm /tmp/jboss5.1.0.GAbin.tar.gz
ADD scripts/ /cloudunit/scripts
RUN chmod -R +x /cloudunit/scripts

# REPERTOIRE UTILE POUR LOGSTASH
VOLUME /cloudunit/binaries
VOLUME /cloudunit/scripts
