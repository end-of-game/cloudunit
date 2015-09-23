#!/bin/bash

BIN_REPO=https://github.com/Treeptik/cloudunit/releases/download/0.9
BIN_LIST=bin-list

while read FILE; do
      echo "$FILE"
      BASENAME=$(basename "$FILE")
      DIRNAME=$(dirname "$FILE")
      mkdir -p "$DIRNAME"
      wget "$BIN_REPO/$BASENAME" -O "$FILE"
      echo ""
done < "$BIN_LIST"
