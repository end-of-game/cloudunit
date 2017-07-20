#!/bin/bash -ue
##

## CoreOS Clair
echo ">>> Installing CoreOS Clair and clairctl"
docker-compose up -d

## clairctl 
echo ">>> Installing clairctl"
PLATFORM=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)
if [[ "$(uname -m)" == *"64"* ]]; then ARCH="amd64"; else ARCH="386"; fi
echo $PLATFORM
echo $ARCH

curl -o /usr/local/bin/clairctl "https://s3.amazonaws.com/clairctl/latest/clairctl-$PLATFORM-$ARCH"
chmod +x /usr/local/bin/clairctl

clairctl health --log-level debug

exit 0
