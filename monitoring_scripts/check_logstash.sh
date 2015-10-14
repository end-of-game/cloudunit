#!/bin/bash

sudo service logstash status

RETURN=$(echo $?)

if [ $RETURN == 3 ]; then
	exit 2
fi

exit $RETURN
