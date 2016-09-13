# docker build -t cloudunit/mysql-5-5 .
# Run like "docker run -d cloudunit/mysql-5-5"

FROM cloudunit/base-14.04

ENV DEBIAN_FRONTEND noninteractive 

LABEL origin cloudunit

RUN echo "deb http://archive.ubuntu.com/ubuntu precise main universe" >> /etc/apt/sources.list
RUN apt-get update

RUN echo mysql-server-5.5 mysql-server/root_password password 'root' | debconf-set-selections
RUN echo mysql-server-5.5 mysql-server/root_password_again password 'root' | debconf-set-selections

ENV MYSQL_MAJOR 5.5
# ENV MYSQL_VERSION 5.5.49-0ubuntu0.14.04.1
# RUN apt-get install -y mysql-server-$MYSQL_MAJOR
RUN apt-get install -y mysql-server-$MYSQL_MAJOR

RUN chown mysql:mysql /var/run/mysqld
RUN sed -i -e"s/^bind-address\s*=\s*127.0.0.1/bind-address = 0.0.0.0/" -e"s/\/var\/lib\/mysql/\/cloudunit\/database/g" -e"/skip-external-locking/ a\lower_case_table_names = 1" /etc/mysql/my.cnf
ADD php.ini /etc/php5/apache2/

RUN echo 'phpmyadmin phpmyadmin/dbconfig-install boolean true' | debconf-set-selections
RUN echo 'phpmyadmin phpmyadmin/app-password-confirm password root' | debconf-set-selections
RUN echo 'phpmyadmin phpmyadmin/mysql/admin-pass password root' | debconf-set-selections
RUN echo 'phpmyadmin phpmyadmin/mysql/app-pass password root' | debconf-set-selections
RUN echo 'phpmyadmin phpmyadmin/reconfigure-webserver multiselect apache2' | debconf-set-selections
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y phpmyadmin

# VARIABLES ENVIRONNEMENT
ENV CU_DATABASE_HOME /cloudunit/database
RUN mkdir /cloudunit/database
RUN echo -n "CU_DATABASE_HOME=$CU_DATABASE_HOME\n" >> /etc/environment

# Add custom scripts
ADD scripts /cloudunit/scripts/

# expose datadir as volume for performance cow
VOLUME /cloudunit/database

# SSH
EXPOSE 22	

ENTRYPOINT ["/bin/bash", "/cloudunit/scripts/start-service.sh"]

