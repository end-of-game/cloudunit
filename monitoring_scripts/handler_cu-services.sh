#!/bin/bash

dir=/home/admincu/cloudunit/cu-services
file_err=/tmp/service_cont_not_launched
file_cont=$dir/run-services.sh

cont=`cat $file_err`
echo "Launching docker container $cont."

`grep $cont $file_cont`

rm $file_err
