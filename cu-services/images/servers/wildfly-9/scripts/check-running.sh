#!/bin/bash

CU_USER=$1
CU_PASSWORD=$2

$JBOSS_HOME/bin/jboss-cli.sh -c --user=$CU_USER --password=$CU_PASSWORD --command="connect"

RETURN=$?

# The echo is used by docker-exec to know if results are right
# do not remove it
echo $RETURN
