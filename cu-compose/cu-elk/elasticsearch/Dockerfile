FROM elasticsearch:5.0.0

COPY config ./config

COPY docker-entrypoint.sh /

LABEL origin cloudunit-monitoring
LABEL application-type elasticsearch
LABEL application-version $ELASTICSEARCH_VERSION