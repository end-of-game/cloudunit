#!/usr/bin/env bash

# Local execution for maven
cd ../../cu-manager
# Profil is defined with spring annotation @ActiveProfil
mvn clean test -Dtest=$1
