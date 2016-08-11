#!/usr/bin/env bash

set -x

# Stop the server
sh $CATALINA_HOME/bin/catalina.sh stop

rm -rf $CU_LOGS/*





