#!/bin/sh
echo "$1:$2" | chpasswd

sed --in-place "s/$CU_USER=$CU_PASSWORD/$1=$2/" $JBOSS_HOME/server/default/conf/props/jmx-console-users.properties
sed --in-place "s/$CU_USER=/$1=/" $JBOSS_HOME/server/default/conf/props/jmx-console-roles.properties

sed -i -e 's/CU_PASSWORD='$CU_PASSWORD'/CU_PASSWORD='$2'/g' /etc/environment
