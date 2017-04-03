#!/bin/bash

if [[ $1 == "clean" ]]; then
  exec rm -rf bower_components node_modules dist
fi

if [[ $1 == "install" ]]; then
  exec npm install
fi

if [[ $1 == "run" ]]; then
  exec grunt serve
fi

exec "$@"
