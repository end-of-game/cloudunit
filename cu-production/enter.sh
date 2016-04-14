#!/usr/bin/env bash

if [ "$(uname)" == "Darwin" ]; then
    vagrant ssh mac
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    vagrant ssh linux
fi