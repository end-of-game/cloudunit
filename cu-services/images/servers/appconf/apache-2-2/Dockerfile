FROM cloudunit/base-12.04

ENV APACHE_RUN_USER www-data
ENV APACHE_RUN_GROUP www-data
ENV APACHE_LOG_DIR /var/log/apache2

ENV VERSION 2.2.22-1ubuntu1.10

RUN apt-get update \
    && apt-get -y install   apache2=$VERSION \
                            libapache2-mod-php5 \
                            libapache2-mod-python \
                            libapache2-mod-proxy-html \
                            php5-mysql -y \
                            php5-pgsql -y \
    && apt-get clean && rm -rf /var/lib/apt/lists/* \
    && apt-get update \
    && apt-get install php5-mysql -y \
    && apt-get install php5-pgsql -y

RUN a2enmod php5
RUN a2enmod python
RUN a2enmod proxy
RUN a2enmod proxy_http

EXPOSE 80
EXPOSE 22

# add custom scripts
ADD scripts /cloudunit/scripts
RUN chmod +x /cloudunit/scripts/*
RUN /cloudunit/scripts/envPHP.sh

RUN mkdir -p /cloudunit/appconf/logs/
RUN ln -s /var/log/apache2/* /cloudunit/appconf/logs/

VOLUME /var/log
VOLUME /cloudunit/shared

ENTRYPOINT ["/cloudunit/scripts/start-service.sh"]
