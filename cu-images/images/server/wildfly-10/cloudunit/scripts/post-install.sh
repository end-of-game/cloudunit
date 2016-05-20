#!/usr/bin/env bash

/opt/jboss/wildfly/bin/add-user.sh admin admin --silent

wget https://github.com/Treeptik/cloudunit/releases/download/1.0/cloudunitAgent-1.0-SNAPSHOT.jar -O /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar


