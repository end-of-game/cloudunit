#!/bin/bash

grep -q 'Server startup in' $CU_LOGS/catalina.log
RETURN=$?

# The echo is used by docker-exec to know if results are right
# do not remove it
echo $RETURN
