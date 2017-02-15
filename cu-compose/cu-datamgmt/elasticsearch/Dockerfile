FROM elasticsearch:5.2
ENV $ELASTICSEARCH_VERSION 5.2

HEALTHCHECK CMD curl --fail http://localhost:9200/ || exit 1

COPY config ./config

LABEL origin cloudunit-monitoring
LABEL application-type elasticsearch
LABEL application-version $ELASTICSEARCH_VERSION
