#!/usr/bin/env bash

export JK_MAJOR=2.6

docker images |grep jenkinsci/jenkins |grep '$JK_MAJOR'
if [ "$?" == "1" ]; then
    docker pull jenkinsci/jenkins:$JK_MAJOR
fi

RETURN=`docker ps | grep jenkins2`

# If jenkins is not running
if [ -z "$RETURN" ]; then

    mkdir -p /home/vagrant/jenkins_home
    sudo chown -R vagrant jenkins_home/
    docker run  --name jenkins2 \
                -d -p 9080:8080 -p 50000:50000 \
                -v /home/vagrant/jenkins_home:/var/jenkins_home \
                jenkinsci/jenkins:$JK_MAJOR

    # Maybe it could already exist
    if [ "$?" == "1" ]; then
        sudo chown -R vagrant /home/vagrant/jenkins_home
        docker start jenkins2
    fi
fi

echo -e "\nThink about 'docker logs -f jenkins2' to get password\n"
echo -e "*****************************************************"
echo -e "* ACCESS TO JENKINS AT --> http://192.168.50.4:9080"
echo -e "*****************************************************"
