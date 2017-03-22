#!/bin/bash

CU_USER=$1
CU_PASSWORD=$2
NAME=$3
DRIVER=$4

bash $JBOSS_HOME/bin/jboss-cli.sh --user=$CU_USER --password=$CU_PASSWORD -c --command="/subsystem=datasources/data-source=$NAME:add(jndi-name=java:jboss/datasources/$NAME, driver-name=$DRIVER, connection-url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1)"
