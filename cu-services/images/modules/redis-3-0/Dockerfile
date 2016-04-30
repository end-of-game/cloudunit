FROM cloudunit/base-14.04

ENV DEBIAN_FRONTEND noninteractive
ENV REDIS_VERSION 3.0.1

# VARIABLES ENVIRONNEMENT
ENV CU_DATABASE_HOME /cloudunit/database
RUN mkdir /cloudunit/database
RUN echo -n "CU_DATABASE_HOME=$CU_DATABASE_HOME\n" >> /etc/environment

RUN curl -sL https://deb.nodesource.com/setup | sudo bash -
RUN apt-get install -yq apache2 build-essential nodejs

# Installation de redis
RUN wget http://download.redis.io/releases/redis-$REDIS_VERSION.tar.gz -O /tmp/redis-$REDIS_VERSION.tar.gz
RUN tar xvzf /tmp/redis-$REDIS_VERSION.tar.gz -C /tmp && \
  cd /tmp/redis-$REDIS_VERSION && \
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
  sed -i 's/appendonly no/appendonly yes/g' /etc/redis/redis.conf

RUN groupadd -r redis && useradd -r -g redis redis

# Installation de redis-commander
RUN npm install -g redis-commander

# Add custom scripts
ADD scripts /cloudunit/scripts/
RUN chmod +x /cloudunit/scripts/*

# expose datadir as volume for performance cow
VOLUME /cloudunit/database

## PORTS A EXPOSER
EXPOSE 22

ENTRYPOINT ["/bin/bash", "/cloudunit/scripts/start-service.sh"]
