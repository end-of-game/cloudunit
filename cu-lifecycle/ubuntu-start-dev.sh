#!/bin/bash


function start_vagrant_docker {
	cd $HOME/cloudunit/cu-vagrant
	vagrant halt
	vagrant up
	vagrant ssh -c "/home/vagrant/cloudunit/cu-platform/reset.sh -y"
}

function start_java {
	gnome-terminal --tab -e 'bash -c "cd $HOME/cloudunit/cu-manager;mvn -q clean compile tomcat7:run -DskipTests -Dspring.profiles.active=vagrant;"'
}

function start_front {
	gnome-terminal --tab -e 'bash -c "cd $HOME/cloudunit/cu-manager/src/main/webapp && grunt serve;"'
}


start_vagrant_docker
start_java
start_front
