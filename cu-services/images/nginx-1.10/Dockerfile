FROM nginx:1.10.3

RUN apt-get update \
	&& apt-get install --no-install-recommends --no-install-suggests -y curl netcat \
	&& rm -rf /var/lib/apt/lists/*

ENV CU_SOFTWARE /usr/share/nginx/html
ENV CU_SERVER_MANAGER_PATH ""
ENV CU_SERVER_MANAGER_PORT ""
ENV CU_SERVER_PORT 80
ENV CU_SERVER_RESTART_POST_CREDENTIALS false
ENV CU_DEFAULT_LOG_FILE system.out
ENV CU_LOGS stdout

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*

# Enable server status app
RUN sed -i '2i		location /server-status {\n		stub_status on;\n		access_log   off;\n		allow 127.0.0.1;\n		deny all;\n	}\n' /etc/nginx/conf.d/default.conf


COPY docker-entrypoint.sh /usr/local/bin/
ENTRYPOINT ["docker-entrypoint.sh"]

CMD [ "nginx" ]

LABEL origin cloudunit
LABEL application nginx
LABEL version $NGINX_VERSION
