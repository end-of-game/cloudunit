FROM elasticsearch:5.1.1
HEALTHCHECK CMD curl --fail http://localhost:9200/ || exit 1

COPY config ./config

COPY docker-entrypoint.sh /

LABEL origin cloudunit-monitoring
LABEL application-type elasticsearch
LABEL application-version $ELASTICSEARCH_VERSION
