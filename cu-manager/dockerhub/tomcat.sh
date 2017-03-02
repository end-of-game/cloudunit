#!/bin/bash

export JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=production"
exec catalina.sh run