FROM cloudunit/jboss-base

#
# Installation de Jboss
#
RUN wget https://github.com/Treeptik/cloudunit/releases/download/0.9/jboss8bin.tar.gz -O /tmp/jboss8bin.tar.gz \
	&& tar -xvf /tmp/jboss8bin.tar.gz -C /cloudunit/binaries && rm /tmp/jboss8bin.tar.gz
ADD scripts/ /cloudunit/scripts
RUN chmod -R +x /cloudunit/scripts

# REPERTOIRE UTILE POUR LOGSTASH
VOLUME /cloudunit/binaries
VOLUME /cloudunit/scripts
