#!/bin/bash

if [[ $CU_SOFTWARE == "" ]]; then
  node /index.js
else
  node $CU_SOFTWARE
fi

exec "$@"
