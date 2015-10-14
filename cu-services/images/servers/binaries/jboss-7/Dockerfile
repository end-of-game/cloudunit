FROM cloudunit/jboss-base

#
# Installation de Jboss
#
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jboss7bin.tar.gz -O /tmp/jboss7bin.tar.gz \
	&& tar -xvf /tmp/jboss7bin.tar.gz -C /cloudunit/binaries && rm /tmp/jboss7bin.tar.gz
ADD scripts/ /cloudunit/scripts
RUN chmod -R +x /cloudunit/scripts

# REPERTOIRE UTILE POUR LOGSTASH
VOLUME /cloudunit/binaries
VOLUME /cloudunit/scripts
