#!/bin/bash

nc -z localhost 9200
RETURN=$?

# The echo is used by docker-exec to know if results are right
# do not remove it
echo $RETURN
