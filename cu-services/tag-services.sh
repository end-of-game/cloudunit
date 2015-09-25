#!/bin/bash

if [ -z "$(git describe --exact-match --tags 2>/dev/null)" ]; then
	GIT_TAG=latest
else
	GIT_TAG=`git describe --exact-match --tags 2>/dev/null`
fi


while read line
do
	image=`echo $line | cut -d" " -f 1`
	repository=`echo $line | cut -d" " -f 2`
	echo -e "\n Tagging image $image:$GIT_TAG."
	dockerhub-tag set $image $GIT_TAG  $GIT_TAG cu-services/$repository
done < cu-images
