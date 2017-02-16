#!/bin/bash

CACHE_STRATEGY="--no-cache"
if [ "$2" == "cache" ]; then
    CACHE_STRATEGY=""
fi

docker build --rm $CACHE_STRATEGY -t cloudunit/datamgmt-filebeat    filebeat
