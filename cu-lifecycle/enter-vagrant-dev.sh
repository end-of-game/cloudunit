#!/usr/bin/env bash

source ./common.sh

# check the CU_HOME env variable
check_cu_home

# display logo
logo

cd $CU_HOME/cu-vagrant
vagrant ssh
