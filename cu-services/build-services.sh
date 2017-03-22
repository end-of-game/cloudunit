#!/bin/bash

CACHE_STRATEGY="--no-cache"
if [ "$2" == "cache" ]; then
    CACHE_STRATEGY=""
fi

function base {
    docker build --rm $CACHE_STRATEGY -t cloudunit/base-jessie images/base-jessie
    docker build --rm $CACHE_STRATEGY -t cloudunit/base-12.04 images/base-12.04
    docker build --rm $CACHE_STRATEGY -t cloudunit/base-14.04 images/base-14.04
    docker build --rm $CACHE_STRATEGY -t cloudunit/base-16.04 images/base-16.04
}

function nginx {
    docker build --rm $CACHE_STRATEGY -t cloudunit/nginx-1.10 images/servers/nginx-10
}

function apache {
    docker build --rm $CACHE_STRATEGY -t cloudunit/apache-2-2 images/servers/apache-2-2
}

function glassfish {
    docker build --rm $CACHE_STRATEGY -t cloudunit/glassfish4 images/servers/glassfish4
}

function tomcat {
    docker build --rm $CACHE_STRATEGY -t cloudunit/tomcat-6     images/servers/tomcat-6
    docker build --rm $CACHE_STRATEGY -t cloudunit/tomcat-7     images/servers/tomcat-7
    docker build --rm $CACHE_STRATEGY -t cloudunit/tomcat-8     images/servers/tomcat-8
    docker build --rm $CACHE_STRATEGY -t cloudunit/tomcat-85    images/servers/tomcat-85
    docker build --rm $CACHE_STRATEGY -t cloudunit/tomcat-9     images/servers/tomcat-9
}

function fatjar {
    docker build --rm $CACHE_STRATEGY -t cloudunit/fatjar images/servers/fatjar
}

function wildfly {
    docker build --rm $CACHE_STRATEGY -t cloudunit/wildfly-8    images/servers/wildfly-8
    docker build --rm $CACHE_STRATEGY -t cloudunit/wildfly-9    images/servers/wildfly-9
    docker build --rm $CACHE_STRATEGY -t cloudunit/wildfly-10   images/servers/wildfly-10
}

function postgre {
    docker build --rm $CACHE_STRATEGY -t cloudunit/postgresql-9-3 images/modules/postgresql-9-3
    docker build --rm $CACHE_STRATEGY -t cloudunit/postgresql-9-4 images/modules/postgresql-9-4
    docker build --rm $CACHE_STRATEGY -t cloudunit/postgresql-9-5 images/modules/postgresql-9-5
    docker build --rm $CACHE_STRATEGY -t cloudunit/postgis-2-2 images/modules/postgis-2-2
}

function mongo {
    docker build --rm $CACHE_STRATEGY -t cloudunit/mongo-2-6 images/modules/mongo-2-6
}

function mysql {
    docker build --rm $CACHE_STRATEGY -t cloudunit/mysql-5-5 images/modules/mysql-5-5
    docker build --rm $CACHE_STRATEGY -t cloudunit/mysql-5-6 images/modules/mysql-5-6
    docker build --rm $CACHE_STRATEGY -t cloudunit/mysql-5-7 images/modules/mysql-5-7
}

function activemq {
    docker build --rm $CACHE_STRATEGY -t cloudunit/activemq-5.13 images/modules/activemq-5.13
}

function rabbitmq {
    docker build --rm $CACHE_STRATEGY -t cloudunit/rabbitmq-3.6 images/modules/rabbitmq-3.6
}

function elastic {
    docker build --rm $CACHE_STRATEGY -t cloudunit/elasticsearch-2.4 images/modules/elasticsearch-2.4
}

function redis {
    docker build --rm $CACHE_STRATEGY -t cloudunit/redis-3-2 images/modules/redis-3-2
}

function vertx33 {
    docker build --rm $CACHE_STRATEGY -t cloudunit/vertx33-ruby images/servers/vertx33-ruby
    docker build --rm $CACHE_STRATEGY -t cloudunit/vertx33-js images/servers/vertx33-js
    docker build --rm $CACHE_STRATEGY -t cloudunit/vertx33-groovy images/servers/vertx33-groovy
    docker build --rm $CACHE_STRATEGY -t cloudunit/vertx33-java images/servers/vertx33-java
}

case "$1" in

'activemq')
echo "Building ActiveMQ"
activemq
;;

'apache')
echo "Building Apache"
apache
;;

'base')
echo "Building Base Images"
base
;;

'elastic')
echo "Building Elastic"
elastic
;;

'fatjar')
echo "Building FarJar"
fatjar
;;

'mongo')
echo "Building Mongo"
mongo
;;

'mysql')
echo "Building Mysql"
mysql
;;

'nginx')
echo "Building Nginx"
nginx
;;

'postgre')
echo "Building Postgre"
postgre
;;

'rabbitmq')
echo "Building RabbitMQ"
rabbitmq
;;

'redis')
echo "Building Redis"
redis
;;

'tomcat')
echo "Building Tomcat"
tomcat
;;

'tools')
echo "Building Tools"
tools
;;

'vertx33')
echo "Building Vertx"
vertx33
;;

'wildfly')
echo "Building WildFly"
wildfly
;;

'all')
echo "Building all"
base
activemq
apache
elastic
fatjar
mongo
mysql
nginx
postgre
rabbitmq
redis
tomcat
vertx33
wildfly
;;

*)
echo ""
echo "Usage $0 choice [cache]"
echo "By default cache is disabled"
echo "Example : $0 tomcat cache"
echo "Example : $0 mysql"
echo "Choice between : "
echo "                    apache"
echo "                    activemq"
echo "                    all"
echo "                    base"
echo "                    elastic"
echo "                    fatjar"
echo "                    mongo"
echo "                    mysql"
echo "                    nginx"
echo "                    postgre"
echo "                    rabbitmq"
echo "                    redis"
echo "                    tomcat"
echo "                    tools"
echo "                    vertx33"
echo "                    wildfly"
echo ""
;;

esac



