#!/bin/sh

export CU_USER=$1
export CU_PASSWORD=$2

# Lancement serveur openssh
useradd $CU_USER && echo "$CU_USER:$CU_PASSWORD" | chpasswd && echo "root:$CU_PASSWORD" | chpasswd
/usr/sbin/sshd
#cron

tail -f /var/log/faillog
