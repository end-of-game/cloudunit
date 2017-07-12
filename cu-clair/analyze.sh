#!/bin/sh

# run the local stack
docker-compose up -d

clairctl version
clairctl health --config clairctl.yml --log-level debug

echo "Analyse all CloudUnit images"
# get all docker images name
NAME=$(docker images | grep cloudunit | awk 'BEGIN { OFS = ":" }{ print $1, $2 }')
printf "$NAME"
echo ""

# launch clairctl push, pull, analyze,
# report to html to the registry:2 with debug mode
# to hide debug mode, remove --log-level debug 
for arg in $NAME ; do
    sudo clairctl push -l $arg --config clairctl.yml --log-level debug
	sudo clairctl pull -l $arg --config clairctl.yml
	echo ""
	sudo clairctl analyze -l $arg --config clairctl.yml
	echo ""
	sudo clairctl report -l $arg --config clairctl.yml
	echo "***********************"
done

# Remove registry
echo "Removing local docker registry"
docker rm -vf clair-registry

exit 0