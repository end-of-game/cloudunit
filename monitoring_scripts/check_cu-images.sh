#!/bin/bash

FILE=/home/admincu/cloudunit/cu-services/cu-images

while read line
do
    image=`echo $line | cut -d" " -f 1`
    docker images | awk '{print $1}' | grep -q "^$image$"
    return=$?
    if [ "$return" -ne "0" ]; then
        echo "WARNING: the docker image $image has not been pulled!"
        echo "$image" > /tmp/image_not_built
        exit 1
    fi
done < $FILE

echo "OK: All CU images have been correctly pulled."
