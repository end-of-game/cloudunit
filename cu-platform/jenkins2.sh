#!/usr/bin/env bash

export JK_MAJOR=2.21

docker images |grep jenkinsci/jenkins |grep $JK_MAJOR
if [ "$?" == "1" ]; then
    docker pull jenkinsci/jenkins:$JK_MAJOR
fi

RETURN=`docker ps | grep jenkins2`

# If jenkins is not running
if [ -z "$RETURN" ]; then

    mkdir -p /home/$USER/jenkins_home
    sudo chown -R $USER /home/$USER/jenkins_home/

    if [ "$USER" == "vagrant" ];
    then
    	docker run -d --name jenkins2 \
                --restart always \
                -p 9080:8080 -p 50000:50000 \
                -v /home/$USER/jenkins_home:/var/jenkins_home \
                jenkinsci/jenkins:$JK_MAJOR
    else
	    docker run -d --name jenkins2 \
                --restart always \
                -v /home/$USER/jenkins_home:/var/jenkins_home \
                jenkinsci/jenkins:$JK_MAJOR
 	        docker-compose -f docker-compose-prod.yml rm -f nginx
            docker-compose -f docker-compose-prod.yml up -d nginx	 
    fi

    # Maybe it could already exist
    if [ "$?" == "1" ]; then
        sudo chown -R $USER /home/$USER/jenkins_home
        docker start jenkins2
    fi
fi

echo -e "\nThink about 'docker logs -f jenkins2' to get password\n"
echo -e "************************************************************"
echo -e "* ACCESS TO JENKINS AT --> http://192.168.50.4:9080 IF DEV "
echo -e "* ACCESS TO JENKINS AT --> https://$CU_JENKINS_URL IF PROD "
echo -e "************************************************************"
