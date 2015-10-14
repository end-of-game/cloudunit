#!/bin/bash

cut -d" " -f 1 cu-images > cu-images-name
while read line
do
	echo $line
	TAR_NAME="echo $line.tar | sed 's/cloudunit\///'"
	eval $TAR_NAME
	docker save -o tar-images/$(eval $TAR_NAME) $line
done < cu-images-name

rm cu-images-name
