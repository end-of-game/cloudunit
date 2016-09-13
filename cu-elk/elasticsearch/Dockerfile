FROM elasticsearch:5.0.0-alpha5

MAINTAINER s.musso@treeptik.fr

COPY config ./config
RUN set -x \
    && chown -R elasticsearch:elasticsearch ./config
########################
## CLOUDUNIT SPECIFIC ##
########################

ENV CLOUDUNIT_DESCRIPTION "Elasticsearch 5 with OpenJDK8"

# copy the cloudunit lifecycle scripts
RUN set -x \
    && mkdir /opt/cloudunit
COPY scripts /opt/cloudunit/scripts
RUN set -x \
    && chmod a+x /opt/cloudunit/scripts/*

ENTRYPOINT ["/opt/cloudunit/scripts/docker-entrypoint.sh"]
CMD ["elasticsearch", "-E", "cluster.name=cu-mon", "-E", "node.name=elasticsearch", "-E", "bootstrap.ignore_system_bootstrap_checks=true"]
