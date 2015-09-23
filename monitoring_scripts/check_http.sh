#!/bin/bash

if [ -z "$2" ]; then
	HOST=localhost
else
	HOST=$2
fi

echo "HOST=$HOST PORT=$1"

OUTPUT=$(curl --max-time 10 -sS http://$HOST:$1)

RETURN=$(echo $?)

if [ "$RETURN" != 0 ]; then
	echo $OUTPUT
	exit 2 
else
	echo "OK"
fi

