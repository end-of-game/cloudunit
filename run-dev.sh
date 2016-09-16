#!/usr/bin/env bash

# ###############################################################################
# ###############################################################################
# ###############################################################################

# ssh-keygen (create a 'vagrant' entry)
# ssh-copy-id vagrant@192.168.50.4 (password: vagrant)

# ###############################################################################
# ###############################################################################
# ###############################################################################

function compile_and_install_parent {
    mvn clean install -DskipTests
}

function run_webui {
    cd $CLOUDUNIT_HOME/cu-manager-ui
    grunt serve
}

function run_tomcat {
    cd $CLOUDUNIT_HOME/cu-manager
    mvn clean compile tomcat7:run -DskipTests -Dspring.profiles.active=vagrant
}

function reset_vagrant {
    cd $CLOUDUNIT_HOME/cu-vagrant
    ssh 'vagrant@192.168.50.4' "cd cloudunit/cu-platform && ./reset.sh -y"
}

function usage {
    echo "Usage: $0 (tomcat|reset)"
    echo "  tomcat : run tomcat without ide"
    echo "  reset  : delete all containers, volumes and database into vagrantbox"
	exit 1
}

function check_env {
    if [ "$USER" == "vagrant" ];
    then
        echo "Needed to be executed outside the vagrant box. Please exit"
        exit 1
    fi
}

# ###############################################################################
# ###############################################################################
# ###############################################################################

export CLOUDUNIT_HOME=`pwd`

check_env
compile_and_install_parent

while test $# -gt 0
do
    case "$1" in
        tomcat)
            run_tomcat
            ;;
        reset)
            reset_vagrant
            ;;
        *) echo "bad option $1"
    	   usage
            ;;
    esac
    shift
done

run_webui 2>&1 &

