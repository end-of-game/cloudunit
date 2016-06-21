#!/usr/bin/env bash

export GL_MAJOR=8.8.3-ce.0

docker images |grep gitlab/gitlab-ce |grep $GL_MAJOR
if [ "$?" == "1" ]; then
    docker pull gitlab/gitlab-ce:$GL_MAJOR
fi

ETURN=`docker ps | grep gitlab`

# If jenkins is not running
if [ -z "$RETURN" ]; then

    docker run --detach \
        --hostname gitlab.cloudunit.serv \
        --publish 4443:443 --publish 480:80 --publish 422:22 \
        --name gitlab \
        --restart always \
        --volume /home/vagrant/gitlab_home/config:/etc/gitlab \
        --volume /home/vagrant/gitlab_home/logs:/var/log/gitlab \
        --volume /home/vagrant/gitlab_home/data:/var/opt/gitlab \
        gitlab/gitlab-ce:$GL_MAJOR

    # Maybe it could already exist
    if [ "$?" == "1" ]; then
        docker start gitlab
    fi
fi

echo -e "***************************************************"
echo -e "* ACCESS TO GITLAB AT --> http://192.168.50.4:480"
echo -e "***************************************************"

