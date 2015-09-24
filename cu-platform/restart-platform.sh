#!/bin/bash

LOCK=/tmp/cu-platform.lock

date
echo "exécution de restart-platform"
if [ -e "$LOCK" ]; then
	echo "restart-platform est déjà en train d'être exécuté"
	exit 1
else
	touch $LOCK
	docker-compose stop && ./start-platform.sh
	rm $LOCK
	echo "fin d'exécution de restart-platform"
fi
