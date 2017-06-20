#!/bin/bash

if [[ $1 == "run" ]]; then
  exec catalina.sh "run"
fi

exec "$@"
