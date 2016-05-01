#!/usr/bin/env bash

source ./common.sh

# check the CU_HOME env variable
check_cu_home

cd $CU_HOME/cu-vagrant
vagrant halt

display_shutdown




