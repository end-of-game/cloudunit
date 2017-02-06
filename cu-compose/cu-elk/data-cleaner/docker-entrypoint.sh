#!/bin/bash

echo 'Waiting for elasticsearch Up and Running'
until $(curl --output /dev/null --silent --head --fail $ELASTICSEARCH_URL); do
  sleep 2s
done

./data-cleaner
