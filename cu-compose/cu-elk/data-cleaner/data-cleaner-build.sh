#!/bin/bash

docker build -t data-cleaner-builder src/
docker run -v "$PWD"/src:/usr/src/data-cleaner/ -w /usr/src/data-cleaner data-cleaner-builder go build
mv src/data-cleaner .
