#!/bin/bash

dir=~/.m2

if [ ! -d "$dir" ]; then
	mkdir $dir
fi

for dir in cu-nodebuild cu-javabuild
do
	git checkout images/$dir/Dockerfile
	sed --in-place "s/builder_uid/$(id -u)/g;s/builder_gid/$(id -g)/g" images/$dir/Dockerfile
done

docker-compose build
docker-compose up cunodebuild
docker-compose up cujavabuild

for dir in cu-nodebuild cu-javabuild
do
	git checkout images/$dir/Dockerfile
done
