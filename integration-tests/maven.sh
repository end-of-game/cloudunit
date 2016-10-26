#!/usr/bin/env bash

cd ../cu-manager

# Profil is defined with spring annotation @ActiveProfil
mvn clean test -Dtest=$1
