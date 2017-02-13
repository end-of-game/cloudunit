FROM alpine:3.5
MAINTAINER s.musso@treeptik.fr

ENV DATAMGMT_MANAGER_VERSION 0.1

## Get DATAMGMT MANAGER binaries
RUN mkdir -p /opt/datamgmt/manager \
    && apk update && apk add curl glib \
    && cd /opt/datamgmt/manager \
    && rm -rf /var/cache/apk/*

WORKDIR /opt/datamgmt/manager

COPY datamgmt .

COPY docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]

LABEL origin datamgmt
LABEL application-type manager
LABEL application-version $DATAMGMT_MANAGER_VERSION
