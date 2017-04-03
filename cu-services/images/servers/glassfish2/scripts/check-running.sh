#!/bin/bash


grep -q 'Application server startup complete' $CU_LOGS/server.log
RETURN=$?

# The echo is used by docker-exec to know if results are right
# do not remove it
echo $RETURN
