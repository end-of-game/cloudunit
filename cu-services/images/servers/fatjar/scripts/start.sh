#!/bin/bash

FILETODEPLOY=`ls $CU_SOFTWARE/deployments`

$JAVA_HOME/bin/java -jar $CU_SOFTWARE/deployments/$FILETODEPLOY > $CU_LOGS/system.out &

echo "JVM is started"

# wait indefinetely
while true
do
  tail -f /dev/null & wait ${!}
done
