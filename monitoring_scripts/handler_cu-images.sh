#!/bin/bash

dir=/home/admincu/cloudunit/cu-services
file_list_im=$dir/cu-images
file_err_im=/tmp/image_not_built

image=`cat $file_err_im`

cd $dir
if [ -z "$(git describe --exact-match --tags 2>/dev/null)" ]; then
	GIT_TAG=latest
else
	GIT_TAG=`git describe --exact-match --tags 2>/dev/null`
fi


echo "Pulling docker image $image:$GIT_TAG."
docker pull $image:$GIT_TAG

rm $file_err_im
