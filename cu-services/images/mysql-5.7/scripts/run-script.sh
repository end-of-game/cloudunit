#!/bin/bash

set -x

CU_FILE=$1

mysql -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <$CU_TMP/$CU_FILE
RETURN=$?

# This echo is used by `docker exec` to know if everything went OK
# Do not remove it
echo $RETURN
