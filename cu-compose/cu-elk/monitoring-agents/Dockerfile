FROM ubuntu:16.04
MAINTAINER s.musso@treeptik.fr

ENV METRICBEAT_VERSION 5.1.1

## Get Metricbeat binaries
RUN mkdir -p /opt/cloudunit/monitoring-agents \
    && apt update && apt -y install wget curl \
    && cd /opt/cloudunit/monitoring-agents \
    && wget https://artifacts.elastic.co/downloads/beats/metricbeat/metricbeat-$METRICBEAT_VERSION-linux-x86_64.tar.gz \
    && tar zxf metricbeat-$METRICBEAT_VERSION-linux-x86_64.tar.gz \
    && mv metricbeat-$METRICBEAT_VERSION-linux-x86_64 metricbeat \
    && rm metricbeat-$METRICBEAT_VERSION-linux-x86_64.tar.gz

RUN useradd metricbeat \
    && chown -R metricbeat:metricbeat /opt/cloudunit/monitoring-agents \
    && apt-get clean \
    && apt-get autoremove \
    && ln -sf /dev/stdout /var/log/metricbeat

RUN set -x \
	&& wget -qO - https://packages.elastic.co/GPG-KEY-elasticsearch | apt-key add - \
	&& echo "deb http://packages.elastic.co/curator/4/debian stable main" > /etc/apt/sources.list.d/elastic.list \
	&& apt update \
	&& apt install elasticsearch-curator \
  && rm -rf /docker docker-$DOCKER_CLIENT.tgz /var/lib/apt/lists/*

COPY curator /etc/curator

RUN set -x \
  && echo "00 2 * * * curator --config /etc/curator/curator.yml /etc/curator/purge-index"

WORKDIR /opt/cloudunit/monitoring-agents/metricbeat
COPY ./docker-entrypoint.sh /
VOLUME /opt/cloudunit/monitoring-agents

LABEL origin cloudunit-monitoring
LABEL application-type monitoring
LABEL application-version 0.1

CMD ["/docker-entrypoint.sh"]
