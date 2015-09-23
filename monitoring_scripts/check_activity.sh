#!/bin/bash

DIR=/home/shinken

if [ ! -e $DIR/messages.current ];then
	touch $DIR/messages.current
fi

mv $DIR/messages.current $DIR/messages.last
./get-mysql-data.sh messages | cut -d$'\t' -f 4,5 > $DIR/messages.current

DIFF=$(diff -f $DIR/messages.last $DIR/messages.current |tail -n +2 | head -n -1)

if (( ${#DIFF} >= 2 ));then
	echo "WARNING: Activity has been detected on CloudUnit"
	echo "$DIFF"
	exit 1
else
	echo "OK: No activity"
	exit 0
fi
