FROM cloudunit/base-14.04

ENV DEBIAN_FRONTEND noninteractive
ENV CU_DATABASE_HOME /cloudunit/database

RUN \
  curl -sL https://deb.nodesource.com/setup | sudo bash - &&\
  apt-get install -yq apache2 build-essential nodejs

# Installation de redis
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/redis-3.0.tar.gz -O /tmp/redis-3.0.tar.gz
RUN tar xvzf /tmp/redis-3.0.tar.gz -C /tmp && \
  cd /tmp/redis-stable && \
  make && \
  make install && \
  cp -f src/redis-sentinel /usr/local/bin && \
  mkdir -p /etc/redis && \
  cp -f *.conf /etc/redis && \
  rm -rf /tmp/redis-3-0* && \
  sed -i 's/^\(bind .*\)$/# \1/' /etc/redis/redis.conf && \
  sed -i 's/^\(daemonize .*\)$/# \1/' /etc/redis/redis.conf && \
  sed -i "s|dir .\/|dir $CU_DATABASE_HOME|" /etc/redis/redis.conf && \
  sed -i 's/^\(logfile .*\)$/# \1/' /etc/redis/redis.conf && \
  sed -i 's/appendonly no/appendonly yes/g' /etc/redis/redis.conf && \
  mkdir $CU_DATABASE_HOME && \
  echo -n "CU_DATABASE_HOME=$CU_DATABASE_HOME\n" >> /etc/environment

# Installation de redis-commander
RUN npm install -g redis-commander
 
VOLUME ["/data"]

ADD scripts /cloudunit/scripts/

WORKDIR /data

## PORTS A EXPOSER
EXPOSE 22 

ENTRYPOINT ["/bin/bash", "/cloudunit/scripts/start-service.sh"]
