
#!/bin/bash

node_dir=src/main/webapp
tomcat_dir=/home/admincu/cloudunit/cu-platform/tomcat

if [ ! -d "$tomcat_dir" ]; then
	mkdir $tomcat_dir
fi

if [ ! -d "$node_dir/node_modules" ]; then
	wget https://github.com/Treeptik/CloudUnit/releases/download/1.0/node_modules.tar.gz -O /tmp/node_modules.tar.gz && tar -xf /tmp/node_modules.tar.gz -C $node_dir
fi

if [ ! -d "$node_dir/bower_components" ]; then
	wget https://github.com/Treeptik/CloudUnit/releases/download/1.0/bower_components.tar.gz -O /tmp/bower_components.tar.gz && tar -xf /tmp/bower_components.tar.gz -C $node_dir
fi


for dir in cu-nodebuild cu-javabuild
do
	git checkout images/$dir/Dockerfile
	sed --in-place "s/builder_uid/$(id -u)/g;s/builder_gid/$(id -g)/g" images/$dir/Dockerfile
done

docker-compose up cunodebuild
docker-compose up cujavabuild

for dir in cu-nodebuild cu-javabuild
do
	git checkout images/$dir/Dockerfile
done
