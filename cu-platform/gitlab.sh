#!/usr/bin/env bash



#!/usr/bin/env bash

export GL_MAJOR=latest

docker images |grep gitlab/gitlab-ce |grep $GL_MAJOR
if [ "$?" == "1" ]; then
    docker pull gitlab/gitlab-ce:$GL_MAJOR
fi

RETURN=`docker ps | grep gitlab`

# If jenkins is not running
if [ "$RETURN" == "1" ]; then

    docker run --detach \
        --hostname gitlab.cloudunit.serv \
        --publish 4443:443 --publish 480:80 --publish 422:22 \
        --name gitlab \
        --restart always \
        --volume /srv/gitlab/config:/etc/gitlab \
        --volume /srv/gitlab/logs:/var/log/gitlab \
        --volume /srv/gitlab/data:/var/opt/gitlab \
        gitlab/gitlab-ce:latest

    # Maybe it could already exist
    if [ "$?" == "1" ]; then
        docker start gitlab
    fi
fi

echo -e "\nThink about 'docker logs -f jenkins2' to get password\n"
echo -e "***************************************************"
echo -e "* ACCESS TO GITLAB AT --> http://192.168.50.4:480"
echo -e "***************************************************"
