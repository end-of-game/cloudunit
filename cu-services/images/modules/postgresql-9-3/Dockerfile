FROM cloudunit/base-12.04

ENV DEBIAN_FRONTEND noninteractive

ENV CU_DATABASE_HOME /cloudunit/database

RUN mkdir /cloudunit/database
RUN echo -n "CU_DATABASE_HOME=$CU_DATABASE_HOME\n" >> /etc/environment

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys B97B0AFCAA1A47F044F244A07FCC7D46ACCC4CF8
RUN echo "deb http://apt.postgresql.org/pub/repos/apt/ precise-pgdg main" > /etc/apt/sources.list.d/pgdg.list
RUN apt-get update

RUN apt-get -y -q install python-software-properties software-properties-common
RUN apt-get -y -q install postgresql-9.3 postgresql-client-9.3 postgresql-contrib-9.3 phppgadmin


RUN chown -R postgres:postgres /cloudunit

## Déplacement du dossier de données
RUN sed -i -e"s:/var/lib/postgresql/9.3/main:/cloudunit/database/main:g" /etc/postgresql/9.3/main/postgresql.conf

## On écoute sur toutes les IP. La sécurité est faite avant. 
RUN echo "listen_addresses='*'" >> /etc/postgresql/9.3/main/postgresql.conf

## Suppression du fichier de configuration d'accès
RUN rm /etc/postgresql/9.3/main/pg_hba.conf 

## Autorisation en mode 'md5' (password demandé) pour les IP autre que localhost
RUN echo "local	all all trust"              >> /etc/postgresql/9.3/main/pg_hba.conf
RUN echo "host	all all 127.0.0.1/32 trust" >> /etc/postgresql/9.3/main/pg_hba.conf
RUN echo "host  all all ::1/128      md5" >> /etc/postgresql/9.3/main/pg_hba.conf
RUN echo "host	all all 0.0.0.0/0    md5"   >> /etc/postgresql/9.3/main/pg_hba.conf

ADD scripts /cloudunit/scripts

EXPOSE 22

ENTRYPOINT ["/bin/bash", "/cloudunit/scripts/start-service.sh"]
