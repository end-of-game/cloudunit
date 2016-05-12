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
    && apt-get clean && rm -rf /var/lib/apt/lists/*

RUN a2enmod php5
RUN a2enmod python
RUN a2enmod proxy
RUN a2enmod proxy_http

EXPOSE 80
EXPOSE 22

# add custom scripts
ADD scripts /cloudunit/scripts
RUN chmod +x /cloudunit/scripts/*

VOLUME /var/log

ENTRYPOINT ["/cloudunit/scripts/start-service.sh"]
