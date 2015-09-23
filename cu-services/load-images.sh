#!/bin/bash

cut -d" " -f 1 cu-images > cu-images-name
while read line
do
	echo $line
	TAR_NAME="echo $line.tar | sed 's/cloudunit\///'"
	eval $TAR_NAME
	docker load -i tar-images/$(eval $TAR_NAME)
done < cu-images-name

rm cu-images-name
