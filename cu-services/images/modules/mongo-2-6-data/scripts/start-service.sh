#!/bin/sh

export CU_USER=$1
export CU_PASSWORD=$2

# Lancement serveur openssh
useradd $CU_USER && echo "$CU_USER:$CU_PASSWORD" | chpasswd && echo "root:$CU_PASSWORD" | chpasswd
/usr/sbin/sshd
#cron

#echo "root:root" | chpasswd
#echo "@hourly root /cloudunit/scripts/backup-data.sh" > /etc/crontab
#echo "@reboot root /cloudunit/scripts/backup-data.sh" >> /etc/crontab

tail -f /var/log/faillog
