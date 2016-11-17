#!/usr/bin/env bash

docker build --rm -t cloudunit/datadog datadog

docker run -d   --name cu-datadog \
                -e API_KEY=3754cdae494a3b44ffab5f32e21e962e \
                -v /proc/:/host/proc/:ro \
                -v /sys/fs/cgroup/:/host/sys/fs/cgroup:ro \
                -v `pwd`/datadog:/conf.d \
                -v /var/run/docker.sock:/var/run/docker.sock \
                cloudunit/datadog:latest

#docker exec $(docker ps -qf "name=cu-datadog") service datadog-agent info
