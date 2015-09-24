#!/bin/bash

if [ "$1" = "CRITICAL" ] || [ "$1" = "WARNING" ]; then
	echo shinken | sudo -S service $2 restart
fi
