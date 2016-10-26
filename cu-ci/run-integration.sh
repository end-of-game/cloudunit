#!/usr/bin/env bash

rm -rf /home/admincu/cloudunit
cd /home/admincu && git clone https://github.com/Treeptik/cloudunit.git -b dev
/home/admincu/cloudunit/cu-services/die-hard.sh
cd /home/admincu/cloudunit/cu-services && ./build-services.sh
