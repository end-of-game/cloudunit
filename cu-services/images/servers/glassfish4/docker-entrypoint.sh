#!/bin/bash

if [[ $1 == "run" ]]; then
  exec asadmin start-domain --verbose
fi

exec "$@"
