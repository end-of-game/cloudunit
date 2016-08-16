#!/usr/bin/env bash

set -x

PID=`pidof java`

# Stop the server
kill $PID


