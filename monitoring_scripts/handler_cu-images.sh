#!/bin/bash

dir=/home/admincu/cloudunit/cu-services
file_list_im=$dir/cu-images
file_err_im=/tmp/image_not_built

image=`cat $file_err_im`
echo "Building docker image $image."
cd $dir
docker build --no-cache -t $(grep "^$image " $file_list_im)

rm $file_err_im
