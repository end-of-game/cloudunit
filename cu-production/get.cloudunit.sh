#!/bin/sh
set -e
#
# This script is meant for quick & easy of cloudunit via:
#   'curl -sSL https://get.cloudunit.io | sh'

check_prerequisite=''
DOCKER_SUPPORTED_MAJOR_VERSION=1.12
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
		exit 1
	fi

	# Check Cpu number
	if [ $(nproc --all) -ge 2 ]; then
		printf "CPU Number \033[1;32m[OK]\033[0m\n"
	else
		printf "CPU Number \033[1;31m[KO]\033[0m\n"
		exit 1
	fi

	# Check host system distribution and version
  distribution=$(cat /etc/issue | cut -c1-6)
	if [ "$distribution" = "Ubuntu" ]; then
		distribution_version=$(cat /etc/issue | cut -c8-12)
		if [ "$distribution_version" = "14.04" ] || [ "$distribution_version" = "16.04" ]; then
			printf "$distribution version $distribution_version \033[1;32m[OK]\033[0m\n"
		else
			printf "Wrong $distribution Version (should be 14.04 or 16.04) \033[1;31m[KO]\033[0m\n"
			exit 1
		fi
	fi

	# Check kernel version
	if [ "$(uname -r | cut -c1)" -ge 4 ]; then
		printf "Kernel version \033[1;32m[OK]\033[0m\n"
	else
		printf "Kernel version sould be 4 or higher please upgrade you kernel \033[1;31m[KO]\033[0m\n"
		exit 1
	fi
}

check_prerequisite

curl -sSL https://raw.githubusercontent.com/Treeptik/cloudunit/dev/cu-production/bootstrap.sh > bootstrap.sh
chmod +x bootstrap.sh
