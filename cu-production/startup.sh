#!/bin/bash

export CU_USER=admincu
export CU_HOME=/home/$CU_USER/cloudunit
export CU_INSTALL_DIR=$CU_HOME/cu-production

if [[ $USER != "$CU_USER" ]]; then
    echo "This script must be run as $CU_USER!"
    exit 1
fi

# BUILD ALL IMAGES
cd $CU_HOME/cu-services && ./build-services.sh all

# INIT THE PLATFORM WITH DOCKER COMPOSE
cd $CU_HOME/cu-compose && ./reset-prod.sh -yes




