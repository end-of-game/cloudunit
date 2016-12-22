#!/bin/sh
set -e
#
# This script is meant for quick & easy of cloudunit via:
#   'curl -sSL https://get.cloudunit.io | sh'

check_prerequisite=''
DOCKER_SUPPORTED_MAJOR_VERSION=1.12
READY_TOGO=1
while [ $# -gt 0 ]; do
	case "$1" in
		--check_prerequisite)
			check_prerequisite="$2"
			shift
			;;
		*)
			echo "Illegal option $1"
			;;
	esac
	shift $(( $# > 0 ? 1 : 0 ))
done

check_prerequisite() {
	# Check memory
	if [ $(free -m | awk '/^Mem:/{print $2}') -ge 7000 ]; then
		printf "Memory Size \033[1;32m[OK]\033[0m\n"
	else
		printf "Memory Size \033[1;31m[KO]\033[0m\n"
	fi

	# Check Cpu number
	if [ $(nproc --all) -ge 2 ]; then
		printf "CPU Number \033[1;32m[OK]\033[0m\n"
	else
		printf "CPU Number \033[1;31m[KO]\033[0m\n"
	fi

	# Check host system distribution and version
  distribution=$(cat /etc/issue | cut -c1-6)
	if [ "$distribution" = "Ubuntu" ]; then
		distribution_version=$(cat /etc/issue | cut -c8-12)
		if [ "$distribution_version" = "14.04" ] || [ "$distribution_version" = "16.04" ]; then
			printf "$distribution version $distribution_version \033[1;32m[OK]\033[0m\n"
		else
			printf "Wrong $distribution Version (should be 14.04 or 16.04) \033[1;31m[KO]\033[0m\n"
			READY_TOGO=0
		fi
	fi

	# Check kernel version
	if [ "$(uname -r | cut -c1)" -ge 4 ]; then
		printf "Kernel version \033[1;32m[OK]\033[0m\n"
	else
		printf "Kernel version sould be 4 or higher please upgrade you kernel \033[1;31m[KO]\033[0m\n"
		READY_TOGO=0
	fi
	# Check if docker is intalled
	if [ ! -f /usr/bin/docker ]; then
  	install_docker
	else
		echo "Docker already installed"
	fi
	docker_version

	# Check docker (/var/lib/docker) free disk space
	df -P -H /var/lib/docker/ | awk 'NR > 1 {print $4+0}'
	if [ "$(df -P -H /var/lib/docker/ | awk 'NR > 1 {print $4+0}')" -ge 30 ]; then
		printf "Disk Space \033[1;32m[OK]\033[0m\n"
	else
		printf "Disk Space for /var/lib/docker should be greather than 30GB \033[1;31m[KO]\033[0m\n"
	fi
}

docker_version() {
	if [ "$(docker info | grep Server | cut -c17-20)" = $DOCKER_SUPPORTED_MAJOR_VERSION ]; then
		echo "Docker version is supported by Cloudunit"
			printf "Docker version \033[1;32m[OK]\033[0m\n"
	else
		printf "Docker version should be at least $DOCKER_SUPPORTED_MAJOR_VERSION \033[1;31m[KO]\033[0m\n"
		READY_TOGO=0
	fi
}

install_docker() {
	if [ "$1" != "-y" ]; then
    echo "Docker is not installed, do you want to install it within this script ? [y/n] (official get.docker.com script)"
    read PROD_ASW
    if [ "$PROD_ASW" != "y" ] && [ "$PROD_ASW" != "n" ]; then
    	echo "Entrer y ou n!"
      exit 1
    elif [ "$PROD_ASW" = "n" ]; then
      exit 1
    fi
  fi
	if [ -f /usr/bin/curl ]; then
  	curl -sSL https://get.docker.com/ | sh
		# > /dev/null 2>&1
	elif [ -f /usr/bin/wget ]; then
		wget -qO- https://get.docker.com/ | sh
		# > /dev/null 2>&1
	else
		echo "Neither curl or wget is installed"
		exit 1
	fi
	if [ -f /usr/bin/docker ]; then
		echo "Docker $(docker info | grep Server | cut -c17-22) have been successfully install"
	else
		echo "Something get wrong check internet connectivity"
	fi
}

check_prerequisite

