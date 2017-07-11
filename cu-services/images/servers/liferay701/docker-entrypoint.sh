#!/bin/bash

env

if [[ $1 == "run" ]]; then
  exec $CATALINA_HOME/bin/catalina.sh "run"
fi

exec "$@"
