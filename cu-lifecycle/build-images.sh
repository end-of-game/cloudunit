#!/usr/bin/env bash

source ./common.sh

# check the CU_HOME env variable
check_cu_home

# display build images logo
display_build_images

cd $CU_HOME/cu-vagrant
vagrant up
vagrant ssh -c "/home/vagrant/cloudunit/cu-platform/reset-all.sh -y"



