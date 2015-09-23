#!/bin/bash

FILE=/home/shinken/volumes

for CONT in tomcat-7 tomcat-6 tomcat-8 jboss-7 jboss-8
do

	echo "$CONT"
	docker inspect -f '{{range $p, $conf := .Volumes}}{{$p}} {{$conf}} {{end}}' $CONT | cut -d" " -f 2 > "$FILE.$CONT"
	docker inspect -f '{{range $p, $conf := .Volumes}}{{$p}} {{$conf}} {{end}}' $CONT | cut -d" " -f 4 >> "$FILE.$CONT"

	while read -r line
	do
		VOLUME=$line
		if [ ! -e $VOLUME ]; then
			echo "A volume from container $CONT is missing!"
			exit 2
		else
			echo "OK - Volumes from cont $CONT are present."
		fi
	done < "$FILE.$CONT"
done

VOLUME=$(docker inspect -f '{{range $p, $conf := .Volumes}}{{$p}} {{$conf}} {{end}}' java | cut -d" " -f 2)
if [ ! -e $VOLUME ]; then
	echo "The volume from container java is missing!"
	exit 2
else
	echo "OK - The volume from container java is present."
fi
