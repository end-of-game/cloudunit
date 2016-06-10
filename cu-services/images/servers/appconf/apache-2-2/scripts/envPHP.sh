#!/bin/sh

echo "SetEnv CU_HOOKS $CU_HOOKS" >> /etc/apache2/conf.d/env-variables.conf
echo "SetEnv CU_JAVA $CU_JAVA" >> /etc/apache2/conf.d/env-variables.conf
echo "SetEnv CU_LOGS $CU_LOGS" >> /etc/apache2/conf.d/env-variables.conf
echo "SetEnv CU_SCRIPTS $CU_SCRIPTS" >> /etc/apache2/conf.d/env-variables.conf
echo "SetEnv CU_USER_HOME $CU_USER_HOME" >> /etc/apache2/conf.d/env-variables.conf

echo "Include conf.d" >> /etc/apache2/httpd.conf
