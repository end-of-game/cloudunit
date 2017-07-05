#!/usr/bin/env bash

jps -v | grep -v Jps | cut -d' ' -f3-
