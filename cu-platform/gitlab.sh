#!/usr/bin/env bash

docker run --detach \
    --hostname gitlab.cloudunit.serv \
    --publish 4443:443 --publish 480:80 --publish 422:22 \
    --name gitlab \
    --restart always \
    --volume /srv/gitlab/config:/etc/gitlab \
    --volume /srv/gitlab/logs:/var/log/gitlab \
    --volume /srv/gitlab/data:/var/opt/gitlab \
    gitlab/gitlab-ce:latest

