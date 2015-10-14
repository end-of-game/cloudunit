#!/bin/bash

docker images | grep 'cloudunit' | cut -d" " -f 1 > images_cu

while read line
do
	docker rmi -f $line
done < images_cu

rm images_cu
