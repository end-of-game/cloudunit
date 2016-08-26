#!/bin/bash

NAME=$1
DRIVER=$2

bash $JBOSS_HOME/bin/jboss-cli.sh -c --command="/subsystem=datasources/data-source=$NAME:add(jndi-name=java:jboss/datasources/$NAME, pool-name=$NAME, driver-name=$DRIVER, connection-url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1)"
