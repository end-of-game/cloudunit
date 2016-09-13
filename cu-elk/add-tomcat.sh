#!/usr/bin/env bash

container=$1

docker exec -ti $container sed -i -e "s/cu-elasticsearch:9200/192.168.50.4:9200/g" /opt/cloudunit/beats-agents/jmxproxybeat/jmxproxybeat.yml

docker exec -d $container /opt/cloudunit/beats-agents/jmxproxybeat/jmxproxybeat

docker exec -ti cu-kibana bash -c 'cd /opt/cloudunit/scripts && node es-init.js '$(docker ps -q --filter name=$container)