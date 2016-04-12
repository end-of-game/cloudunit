#!/usr/bin/env bash

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

{ # Prevent execution if this script was only partially downloaded

set +o posix

  ### Define various error and usage messages
  readonly USAGE="
Usage:
  cloudunit [OPTIONS] [COMMAND]
     -h,        --help               Show this help
     -i,        --install            Install CloudUnit and all dependencies (compose, machine, swarm)
     -u,        --update             Update CloudUnit and all dependencies (compose, machine, swarm)
     -d,        --delete             Delete CloudUnit machine only. Dependencies must be deleted manually (compose, swarm, machine)
     -r,        --reset              Reset the environment. Clean all applications without deleting manager

  Commands:
     start                           Start CloudUnit Server
     stop                            Stop  CloudUnit Server
"

# ==============================================================================================
init () {
    export CLOUDUNIT_VERSION="0.1-alpha"
    export COMPOSE_VERSION=1.3.3
    export MACHINE_VERSION=v0.2.0
    export CLOUDUNIT_HOME=$HOME/.cloudunit
}


# ==============================================================================================
parse_command_line () {
  for command_line_option in "$@"
  do
      case ${command_line_option} in
       -u|--update)
          ## todo
          echo "update :: not yet implemented"
          exit 0
        ;;
       -i|--install)
          installation
        ;;
       -d|--delete)
          delete-machine
        ;;
        -r|--reset)
          reset-machine
        ;;
        clean)
          clean-machine
        ;;
        *)
          # unknown option
          echo "${USAGE}"
        ;;
      esac
  done
}

# ==============================================================================================
delete-machine() {
  docker-machine rm -y cloudunit-master
}

# ==============================================================================================
reset-machine() {
  docker rm -f $(docker ps -aq)
  docker run -d -p 80:8080 -v /var/run/docker.sock:/var/run/docker.sock --name cu-manager cloudunit/manager
  open_browser
}

# ==============================================================================================
installation() {

    # netherless we must check the version too. If already installed, user must manually update it.
    # do not use --version because of X11
    VIRTUALBOX_VERSION=`virtualbox --help | head -n 1 | awk '{print $NF}'`
    if [[ ! "${VIRTUALBOX_VERSION}" == "5."* ]]; then
        echo ""
        echo "You need to install VirtualBox 5 as required !"
        echo "https://www.virtualbox.org/wiki/Linux_Downloads"
        echo ""
        exit -1
    fi

    if [ ! -e "/usr/bin/docker" ]; then
        if [[ "${OSTYPE}" == "linux"* ]]; then
            DISTRO=`lsb_release -d | awk -F"\t" '{print $2}'`
            DISTRO=$(echo $DISTRO | tr "[:upper:]" "[:lower:]")
            if [[ "${DISTRO}" == "ubuntu"* ]]; then
                curl -fsSL https://get.docker.com/ | sh
            fi
            ## TODO : redhat, centos...
        fi
    fi

    if [ ! -e "/usr/local/bin/docker-machine" ]; then
        curl -L https://github.com/docker/machine/releases/download/v0.6.0/docker-machine-`uname -s`-`uname -m` > docker-machine
        chmod a+x docker-machine
        mv docker-machine /usr/local/bin
    fi
    docker-machine create --driver virtualbox cloudunit-master
    eval $(docker-machine env cloudunit-master)
    docker info
    docker pull cloudunit/manager
    docker run -d -p 80:8080 -v /var/run/docker.sock:/var/run/docker.sock --name cu-manager cloudunit/manager
    open_browser
}

# ==============================================================================================
open_browser() {
    MACHINE_IP=`docker-machine ip cloudunit-master`
    # wait for application startup

    count=0;
    RETURN=1

    ## We could wait 10 minutes...
    until [ "$RETURN" -eq "0" ] || [ $count -gt 60 ];
    do
        echo curl $MACHINE_IP
        curl $MACHINE_IP
        RETURN=$?
        sleep 1
        let count=$count+1;
    done

    if [[ "${OSTYPE}" == "linux"* ]]; then
        open google-chrome  http://$MACHINE_IP
        RETURN=$?
        if [[ ! "$RETURN" == "1" ]]; then
            open firefox  http://$MACHINE_IP
        fi
    elif [[ "${OSTYPE}" == "darwin"* ]]; then
        open -a /Applications/Google\ Chrome.app http://$MACHINE_IP
        RETURN=$?
        if [[ ! "$RETURN" == "1" ]]; then
            open -a /Applications/Firefox.app http://$MACHINE_IP
        fi
    fi

    echo "------------------------------------------------------"
    echo "-- CloudUnit access : http://$MACHINE_IP"
    echo "------------------------------------------------------"
}

    init
    parse_command_line "$@"
}

