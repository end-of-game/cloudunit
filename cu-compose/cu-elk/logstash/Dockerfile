FROM logstash:5.0.1
HEALTHCHECK CMD ps -f -u logstash | grep logstash

ENV DOCKER_CLIENT 1.12.3

RUN set -x \
	&& wget -qO - https://packages.elastic.co/GPG-KEY-elasticsearch | apt-key add - \
	&& echo "deb http://packages.elastic.co/curator/4/debian stable main" > /etc/apt/sources.list.d/elastic.list \
	&& wget https://get.docker.com/builds/Linux/x86_64/docker-$DOCKER_CLIENT.tgz \
	&& tar zxf docker-$DOCKER_CLIENT.tgz \
	&& mv /docker/docker /usr/local/bin \
	&& rm -rf /docker docker-$DOCKER_CLIENT.tgz /var/lib/apt/lists/* \
	&& chmod +x /usr/local/bin/docker \
	&& apt update \
	&& apt install elasticsearch-curator \
  && sed -i 's/#path.config/path.config/' /etc/logstash/logstash.yml \
	&& /usr/share/logstash/bin/logstash-plugin install logstash-output-exec

LABEL origin cloudunit-monitoring
LABEL application-type logstash
LABEL application-version $LOGSTASH_VERSION
