#!/bin/sh

if [ -f "/etc/apache2/conf.d/env-variables.conf" ]; then
	echo "SetEnv CU_DATABASE_DNS_MYSQL_1 $CU_DATABASE_DNS_MYSQL_1" >> /etc/apache2/conf.d/env-variables.conf
	echo "SetEnv CU_DATABASE_PASSWORD_MYSQL_1 $CU_DATABASE_PASSWORD_MYSQL_1" >> /etc/apache2/conf.d/env-variables.conf
	echo "SetEnv CU_DATABASE_USER_MYSQL_1 $CU_DATABASE_USER_MYSQL_1" >> /etc/apache2/conf.d/env-variables.conf
fi
