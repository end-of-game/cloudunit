#!/usr/bin/env bash

if [ -f "/opt/cloudunit/tmp/boot.jar" ]; then
    echo "Update application"
    ls -la /opt/cloudunit/fatjar/
    mv -f /opt/cloudunit/tmp/boot.jar /opt/cloudunit/fatjar/boot.jar
    ls -la /opt/cloudunit/fatjar/
fi

# if `docker run` first argument start with `--` the user is passing fatjar launcher arguments
if [[ $# -lt 1 ]] || [[ "$1" == "--"* ]] ; then
  eval "exec java $JAVA_OPTS -jar /opt/cloudunit/fatjar/boot.jar  \"\$@\""
fi

# As argument is not run, assume user want to run his own process, for sample a `bash` shell to explore this image
exec "$@"
