FROM alpine:3.5
MAINTAINER s.musso@treeptik.fr

ENV DATAMGMT_MANAGER_VERSION 0.1

## Get DATAMGMT MANAGER binaries
RUN apk update && apk add curl git glib \
    && mkdir /opt && cd /opt \
    && git clone https://github.com/Treeptik/datamgmt \
    && rm -rf /var/cache/apk/*

WORKDIR /opt/datamgmt/

COPY docker-entrypoint.sh /
ENTRYPOINT ["/docker-entrypoint.sh"]

LABEL origin datamgmt
LABEL application-type manager
LABEL application-version $DATAMGMT_MANAGER_VERSION
