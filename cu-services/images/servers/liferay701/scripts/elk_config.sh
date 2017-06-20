#!/usr/bin/env bash

set -x

IP_ELK=$1

sed -i -e "s/cu-elasticsearch:9200/${IP_ELK}:9200/g" /opt/cloudunit/beats-agents/jmxproxybeat/jmxproxybeat.yml

echo $RETURN

