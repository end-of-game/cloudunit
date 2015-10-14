#!/bin/sh
echo "$1:$2" | chpasswd
sed -i -e 's/CU_PASSWORD='$CU_PASSWORD'/CU_PASSWORD='$2'/g' /etc/environment


### AJOUT DU RM DE L'ANCIEN user password
$JBOSS_HOME/bin/add-user.sh --silent=true $1 $2
