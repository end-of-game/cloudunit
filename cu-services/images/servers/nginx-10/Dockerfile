FROM debian:jessie

MAINTAINER NGINX Docker Maintainers "docker-maint@nginx.com"

ENV NGINX_VERSION 1.10.2-1~jessie

ENV METRICBEAT_VERSION 5.0.1

RUN apt-key adv --keyserver hkp://pgp.mit.edu:80 --recv-keys 573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62 \
	&& echo "deb http://nginx.org/packages/debian/ jessie nginx" >> /etc/apt/sources.list \
	&& apt-get update \
	&& apt-get install --no-install-recommends --no-install-suggests -y \
						ca-certificates \
						nginx=${NGINX_VERSION} \
						nginx-module-xslt \
						nginx-module-geoip \
						nginx-module-image-filter \
						nginx-module-perl \
						nginx-module-njs \
						gettext-base \
						curl \
	&& rm -rf /var/lib/apt/lists/*

# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log \
	&& ln -sf /dev/stderr /var/log/nginx/error.log

EXPOSE 80 443

## CLOUDUNIT :: BEGINNING
ENV CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV CU_SOFTWARE /usr/share/nginx/html
ENV CU_SERVER_MANAGER_PATH /manager/html?
ENV CU_SERVER_MANAGER_PORT 8080
ENV CU_SERVER_PORT 80
ENV CU_SERVER_RESTART_POST_CREDENTIALS false
ENV CU_DEFAULT_LOG_FILE system.out
ENV CU_LOGS stdout

# Install metricbeat agent

RUN mkdir -p /opt/cloudunit/polling-agents/metricbeat \
  && cd /opt/cloudunit/polling-agents \
	&& curl --insecure -O https://artifacts.elastic.co/downloads/beats/metricbeat/metricbeat-$METRICBEAT_VERSION-linux-x86_64.tar.gz \
	&& tar zxvf metricbeat-$METRICBEAT_VERSION-linux-x86_64.tar.gz -C metricbeat --strip-components 1 \
	&& rm metricbeat-$METRICBEAT_VERSION-linux-x86_64.tar.gz \
	&& chown -R nginx:nginx /opt/cloudunit/polling-agents/metricbeat

ADD metricbeat.yml /opt/cloudunit/polling-agents/metricbeat

RUN sed -i '2i		location /server-status {\n		stub_status on;\n		access_log   off;\n		allow 127.0.0.1;\n		deny all;\n	}\n' /etc/nginx/conf.d/default.conf

## CLOUDUNIT :: END

COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]

CMD [ "nginx" ]
