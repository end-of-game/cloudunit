#!/bin/sh

export CU_USER=$1
export CU_PASSWORD=$2

# Lancement serveur openssh
useradd $CU_USER && echo "$CU_USER:$CU_PASSWORD" | chpasswd && echo "root:$CU_PASSWORD" | chpasswd
/usr/sbin/sshd

# Callback bound to the application stop
terminate_handler() {
  exit 0;
}

trap 'terminate_handler' SIGTERM

# Blocking step
while true
do
  tail -f /dev/null & wait ${!}
done

