#!/usr/bin/env bash

export GL_MAJOR=8.8.3-ce.0

docker images |grep gitlab/gitlab-ce |grep $GL_MAJOR
if [ "$?" == "1" ]; then
    docker pull gitlab/gitlab-ce:$GL_MAJOR
fi

ETURN=`docker ps | grep gitlab`

# If jenkins is not running
if [ -z "$RETURN" ]; then

    if [ "$USER" == "vagrant" ];
    then
	    docker run --detach \
        	--hostname gitabl-g2c.cloudunit.io \
        	--publish 4443:443 --publish 480:80 --publish 422:22 \
        	--name gitlab \
        	--restart always \
        	--volume /home/$USER/gitlab_home/config:/etc/gitlab \
        	--volume /home/$USER/gitlab_home/logs:/var/log/gitlab \
        	--volume /home/$USER/gitlab_home/data:/var/opt/gitlab \
        	gitlab/gitlab-ce:$GL_MAJOR
    else
        docker run --detach \
            --hostname ${CU_GITLAB_URL}.cloudunit.io \
            --name gitlab \
            --restart always \
            --volume /home/$USER/gitlab_home/config:/etc/gitlab \
            --volume /home/$USER/gitlab_home/logs:/var/log/gitlab \
            --volume /home/$USER/gitlab_home/data:/var/opt/gitlab \
            gitlab/gitlab-ce:$GL_MAJOR
            docker-compose -f docker-compose-prod.yml rm -f nginx
            docker-compose -f docker-compose-prod.yml up -d nginx
     fi

    # Maybe it could already exist
    if [ "$?" == "1" ]; then
        docker start gitlab
    fi
fi

echo -e "**********************************************************"
echo -e "* ACCESS TO GITLAB AT --> http://192.168.50.4:480 FOR DEV"
echo -e "* ACCESS TO GITLAB AT --> https://$CU_GITLAB_URL FOR PROD"
echo -e "**********************************************************"

