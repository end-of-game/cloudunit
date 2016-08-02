#!/usr/bin/env bash

set -x

export ENV_FILE="/opt/cloudunit/.profile"
source $ENV_FILE

# Stop the server
sh $CATALINA_HOME/bin/catalina.sh stop




