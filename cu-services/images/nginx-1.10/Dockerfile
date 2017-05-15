FROM nginx:1.10.3

RUN apt-get update \
	&& apt-get install --no-install-recommends --no-install-suggests -y curl netcat \
	&& rm -rf /var/lib/apt/lists/*

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

# Enable server status app
RUN sed -i '2i		location /server-status {\n		stub_status on;\n		access_log   off;\n		allow 127.0.0.1;\n		deny all;\n	}\n' /etc/nginx/conf.d/default.conf


COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]

CMD [ "nginx" ]

LABEL io.cloudunit="" \
      io.cloudunit.type=server \
      io.cloudunit.name.service=nginx \
      io.cloudunit.name.display="nginx $NGINX_VERSION" \
      io.cloudunit.version=$NGINX_VERSION
