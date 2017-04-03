#!/usr/bin/env bash

# if `docker run` first argument start with `--` the user is passing launcher arguments
if [[ $1 == "run" ]]; then
  vertx run verticle.java -cp $VERTICLE_HOME/*
fi

# As argument is not run, assume user want to run his own process, for sample a `bash` shell to explore this image
exec "$@"
