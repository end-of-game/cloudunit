# LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
# but CloudUnit is licensed too under a standard commercial license.
# Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
# If you are not sure whether the GPL is right for you,
# you can always test our software under the GPL and inspect the source code before you contact us
# about purchasing a commercial license.

# LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
# or promote products derived from this project without prior written permission from Treeptik.
# Products or services derived from this software may not be called "CloudUnit"
# nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
# For any questions, contact us : contact@treeptik.fr

#!/bin/bash

dir=~/.m2
node_dir=src/main/webapp

if [ ! -d "$dir" ]; then
	mkdir $dir
fi

if [ ! -d "$node_dir/node_modules" ]; then
	wget https://github.com/Treeptik/CloudUnit/releases/download/0.9/node_modules.tar.gz -O /tmp/node_modules.tar.gz && tar -xf /tmp/node_modules.tar.gz -C $node_dir
fi

if [ ! -d "$node_dir/bower_components" ]; then
	wget https://github.com/Treeptik/CloudUnit/releases/download/0.9/bower_components.tar.gz -O /tmp/bower_components.tar.gz && tar -xf /tmp/bower_components.tar.gz -C $node_dir
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
