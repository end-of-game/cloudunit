#!/usr/bin/env bash

export JK_MAJOR=2.1

docker images | grep jenkinsci/jenkins
if [ "$?" == "1" ]; then
    docker pull jenkinsci/jenkins:$JK_MAJOR
fi


docker inspect -f {{.State.Running}} jenkins2

# If jenkins is not running
if [ "$?" == "1" ]; then

    mkdir -p /home/vagrant/jenkins_home

    docker run  --name jenkins2 \
                -d -p 9080:8080 -p 50000:50000 \
                -v /home/vagrant/jenkins_home:/var/jenkins_home \
                jenkinsci/jenkins:$JK_MAJOR

    if [ "$?" == "1" ]; then
        sudo chown -R vagrant /home/vagrant/jenkins_home
        docker start jenkins2
    fi
fi

docker ps -a | grep jenkins2

echo -e "\nACCESS TO JENKINS AT --> http://192.168.50.4:9080\n"
