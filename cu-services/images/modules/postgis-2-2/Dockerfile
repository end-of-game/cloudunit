FROM cloudunit/base-12.04

ENV DEBIAN_FRONTEND noninteractive
ENV CU_DATABASE_HOME /cloudunit/database

# 'apt-cache madison postgresql-9.3' to know all possibilites
ENV PG_MAJOR 9.4
ENV PG_VERSION 9.4.7-1.pgdg12.4+1

ENV POSTGIS_MAJOR 2.2
ENV POSTGIS_VERSION 2.2.2+dfsg-1.pgdg12.4+1

# make the "en_US.UTF-8" locale so postgres will be utf-8 enabled by default
RUN apt-get update && apt-get install -y locales && rm -rf /var/lib/apt/lists/* \
	&& localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8
ENV LANG en_US.utf8

# VARIABLES ENVIRONNEMENT
#ENV CU_DATABASE_HOME /cloudunit/database
#RUN mkdir /cloudunit/database
#RUN echo -n "CU_DATABASE_HOME=$CU_DATABASE_HOME\n" >> /etc/environment

RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -
# RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ precise-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN apt-get update

RUN apt-get -y -q install python-software-properties software-properties-common

RUN apt-get -y -q install postgresql-$PG_MAJOR=$PG_VERSION \
                          postgresql-contrib-$PG_MAJOR=$PG_VERSION \
                          phppgadmin

#RUN apt-get -y -q install postgresql-$PG_MAJOR-postgis-$POSTGIS_MAJOR \
 #                         postgis=$POSTGIS_VERSION

#RUN chown -R postgres:postgres /cloudunit

## Déplacement du dossier de données
#RUN sed -i -e"s:/var/lib/postgresql/9.4/main:/cloudunit/database/main:g" /etc/postgresql/9.4/main/postgresql.conf

## On écoute sur toutes les IP. La sécurité est faite avant. 
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.4/main/postgresql.conf

## Suppression du fichier de configuration d'accès
RUN rm /etc/postgresql/9.4/main/pg_hba.conf

## Autorisation en mode 'md5' (password demandé) pour les IP autre que localhost
RUN echo "local	all all trust"              >> /etc/postgresql/9.4/main/pg_hba.conf
RUN echo "host	all all 127.0.0.1/32 trust" >> /etc/postgresql/9.4/main/pg_hba.conf
RUN echo "host  all all ::1/128      md5" >> /etc/postgresql/9.4/main/pg_hba.conf
RUN echo "host	all all 0.0.0.0/0    md5"   >> /etc/postgresql/9.4/main/pg_hba.conf

# add custom scripts
ADD scripts /cloudunit/scripts
RUN chmod +x /cloudunit/scripts/*

# expose datadir as volume for performance cow
VOLUME /var/lib/postgresql/9.4/main

EXPOSE 22

ENTRYPOINT ["/bin/bash", "/cloudunit/scripts/start-service.sh"]
