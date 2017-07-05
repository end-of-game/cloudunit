#!/usr/bin/env bash

set -x

echo "Execution du script file-exist.sh"

FILE_PATH=$1

if [ -f $FILE_PATH ]; then echo "true"; fi

echo "$FILE_PATH"
echo $FILE_PATH