FROM kibana:5.0.0-alpha5

ENV PATH /usr/share/kibana/node/bin:$PATH

RUN set -x \
    && apt-get update \
	 && apt-get -y install curl \
    && rm -rf /var/lib/apt/lists/* \
    && npm install elasticsearch \
    && kibana-plugin install timelion

########################
## CLOUDUNIT SPECIFIC ##
########################

ENV CLOUDUNIT_DESCRIPTION "Kibana 5 with OpenJDK8"

# copy the cloudunit lifecycle scripts
RUN mkdir /opt/cloudunit
COPY scripts /opt/cloudunit/scripts
RUN chmod a+x /opt/cloudunit/scripts/*

ENTRYPOINT ["/opt/cloudunit/scripts/docker-entrypoint.sh"]
CMD ["kibana", "-e", "http://cuplatform_dnsdock_1.dnsdock.cloud.unit:9200", "-p", "0.0.0.0:5601", "-q"]
