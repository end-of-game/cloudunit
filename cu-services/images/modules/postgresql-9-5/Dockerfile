FROM cloudunit/base-jessie

ENV DEBIAN_FRONTEND noninteractive

ENV PG_MAJOR 9.5
ENV PG_VERSION 9.5.3-1.pgdg80+1
ENV PATH /usr/lib/postgresql/$PG_MAJOR/bin:$PATH
ENV PGDATA /var/lib/postgresql/data
ENV LANG en_US.utf8

RUN groupadd -r postgres --gid=999 && useradd -r -g postgres --uid=999 postgres

# grab gosu for easy step-down from root
ENV GOSU_VERSION 1.7
RUN set -x \
	&& apt-get update && apt-get install -y --no-install-recommends ca-certificates wget && rm -rf /var/lib/apt/lists/* \
	&& wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$(dpkg --print-architecture)" \
	&& wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$(dpkg --print-architecture).asc" \
	&& export GNUPGHOME="$(mktemp -d)" \
	&& gpg --keyserver ha.pool.sks-keyservers.net --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \
	&& gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu \
	&& rm -r "$GNUPGHOME" /usr/local/bin/gosu.asc \
	&& chmod +x /usr/local/bin/gosu \
	&& gosu nobody true \
	&& apt-get purge -y --auto-remove ca-certificates wget

# make the "en_US.UTF-8" locale so postgres will be utf-8 enabled by default
RUN apt-get update && apt-get install -y locales && rm -rf /var/lib/apt/lists/* \
	&& localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8

RUN apt-key adv --keyserver ha.pool.sks-keyservers.net --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8

RUN echo 'deb http://apt.postgresql.org/pub/repos/apt/ jessie-pgdg main' $PG_MAJOR > /etc/apt/sources.list.d/pgdg.list
RUN apt-get update

RUN apt-get -y -q install python-software-properties software-properties-common
RUN apt-get -y -q install postgresql-common \
                          postgresql-$PG_MAJOR=$PG_VERSION \
                          postgresql-contrib-$PG_MAJOR=$PG_VERSION \
                          phppgadmin \
                  && rm -rf /var/lib/apt/lists/*

## On écoute sur toutes les IP. La sécurité est faite avant.
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.5/main/postgresql.conf

## Suppression du fichier de configuration d'accès
RUN rm /etc/postgresql/9.5/main/pg_hba.conf

## Autorisation en mode 'md5' (password demandé) pour les IP autre que localhost
RUN echo "local	all all trust"              >> /etc/postgresql/9.5/main/pg_hba.conf
RUN echo "host	all all 127.0.0.1/32 trust" >> /etc/postgresql/9.5/main/pg_hba.conf
RUN echo "host  all all ::1/128      md5" >> /etc/postgresql/9.5/main/pg_hba.conf
RUN echo "host	all all 0.0.0.0/0    md5"   >> /etc/postgresql/9.5/main/pg_hba.conf

# add custom scripts
ADD scripts /cloudunit/scripts
RUN chmod +x /cloudunit/scripts/*

RUN mkdir -p /var/run/postgresql && chown -R postgres /var/run/postgresql

VOLUME /var/lib/postgresql/data

EXPOSE 22

ENTRYPOINT ["/bin/bash", "/cloudunit/scripts/start-service.sh"]
