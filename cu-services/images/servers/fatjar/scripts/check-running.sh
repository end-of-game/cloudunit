#!/bin/bash

ps -ef | grep java
RETURN=$?

# The echo is used by docker-exec to know if results are right
# do not remove it
echo $RETURN
