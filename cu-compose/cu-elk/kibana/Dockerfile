FROM kibana:5.1.1
HEALTHCHECK CMD curl --fail http://localhost:5601/ || exit 1

ENV PATH /usr/share/kibana/node/bin:$PATH
ENV PATH /usr/share/kibana/bin:$PATH

RUN set -x \
	&& apt-get update \
	&& apt-get install -y curl \
	&& apt-get install -y --no-install-recommends kibana=$KIBANA_VERSION \
	&& npm install -g elasticdump \
	&& perl -pi -e 's/fields/stored_fields/' /usr/share/kibana/node/lib/node_modules/elasticdump/elasticdump.js \
	&& rm -rf /var/lib/apt/lists/* \
	&& sed -ri "s!^(\#\s*)?(elasticsearch\.url:).*!\2 'http://elasticsearch:9200'!" /etc/kibana/kibana.yml \
	&& grep -q 'elasticsearch:9200' /etc/kibana/kibana.yml \
    && sed -i 's/#server.host: "localhost"/server.host: "0.0.0.0"/' /etc/kibana/kibana.yml \
    && sed -i 's/#logging.quiet: false/logging.quiet: true/' /etc/kibana/kibana.yml

WORKDIR /opt/cloudunit/

COPY docker-entrypoint.sh /
COPY kibana-index-tasks.sh .
RUN chmod +x kibana-index-tasks.sh
LABEL origin cloudunit-monitoring
LABEL application-type kibana
LABEL application-version $KIBANA_VERSION
