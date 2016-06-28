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

#!/bin/bash

function verify_java {
	if type -p java; then
		echo found java executable in PATH
		_java=java
	elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
		echo found java executable in JAVA_HOME
		_java="$JAVA_HOME/bin/java"
	else
		echo "no java"
		echo "download it :"

		if [ ! -f java.do ]; then

		wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jdk-8u91-linux-x64.tar.gz -O ${HOME}/jdk-8u91-linux-x64.tar.gz
		tar xvf ${HOME}/jdk-8u91-linux-x64.tar.gz
        rm jdk-8u91-linux-x64.tar.gz

        echo "export JAVA_HOME=${HOME}/jdk1.8.0_91" >> ${HOME}/.bashrc
s
        source ${HOME}/.bashrc

        touch java.do

        fi
	fi

	if [[ "$_java" ]]; then
	    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
	    echo version "$version"
	    if [[ "$version" < "1.8" ]]; then
		echo "version is less than 1.8"
		exit 0
	    fi
	fi
}

LOCK_CM=/tmp/cu-monitor.lock

if [ -e "$LOCK_CM" ]; then
	echo "cloudunitmonitor is currently active"
	exit 1
else
    verify_java
	touch $LOCK_CM
	echo -n -e "\nExécution de cloudunitmonitor.\n"
	if [ ! -f ${HOME}/cloudunit/monitoring_scripts/cloudunitmonitor.jar ]; then
		wget https://github.com/Treeptik/cloudunit/releases/download/1.0/cloudunitmonitor.jar -O ${HOME}/cloudunit/monitoring_scripts/cloudunitmonitor.jar
	fi
	${HOME}/jdk1.8.0_91/bin/java -Xms128m -Xmx128m -jar ${HOME}/cloudunit/monitoring_scripts/cloudunitmonitor.jar $(docker inspect --format {{.NetworkSettings.IPAddress}} cuplatform_mysql_1) ${MYSQL_ROOT_PASSWORD} $(docker inspect --format {{.NetworkSettings.IPAddress}} cuplatform_redis_1) prod http > ${HOME}/cloudunit/monitoring_scripts/cloudunitmonitor.log

	rm $LOCK_CM
fi