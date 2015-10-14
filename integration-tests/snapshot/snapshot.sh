#/bin/bash

set -x

# clean the env
sh ../reset.sh

# run the integration test
sh ../maven.sh Tomcat*SnapshotControllerTestIT
