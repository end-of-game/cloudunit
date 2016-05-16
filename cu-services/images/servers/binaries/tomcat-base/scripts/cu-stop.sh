#!/usr/bin/env bash

set -x

export CATALINA_HOME="/cloudunit/binaries"
export CATALINA_BASE="/cloudunit/appconf"

# Stop the server
sh $CATALINA_HOME/bin/catalina.sh stop




