#!/usr/bin/env bash

source ./common.sh

# check the CU_HOME env variable
check_cu_home

cd $CU_HOME/cu-vagrant
vagrant up
vagrant ssh -c "/home/vagrant/cloudunit/cu-platform/reset.sh -y"

# display logo
display_started


