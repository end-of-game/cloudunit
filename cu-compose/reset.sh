# LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
# but CloudUnit is licensed too under a standard commercial license.
# Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
# If you are not sure whether the GPL is right for you,
# you can always test our software under the GPL and inspect the source code before you contact us
# about purchasing a commercial license.

# LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
# or promote products derived from this project without prior written permission from Treeptik.
# Products or services derived from this software may not be called "CloudUnit"
# nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
# For any questions, contact us : contact@treeptik.fr

#!/usr/bin/env bash

docker rm -vf $(docker ps -aq)
docker-compose up -d dnsdock
sleep 10
docker-compose up -d mysqldata
docker-compose up -d mysql
sleep 10
docker-compose up -d cadvisor
docker-compose up -d hipache
docker-compose up -d redis
docker-compose up -d gitlab
docker-compose up -d jenkins
docker-compose up -d tomcat
docker-compose up -d java
sleep 60
docker-compose up -d nginx
docker-compose logs -f




