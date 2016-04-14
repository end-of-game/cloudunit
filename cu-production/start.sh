#!/usr/bin/env bash

if [ "$(uname)" == "Darwin" ]; then
    vagrant up mac
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    vagrant up linux
fi
