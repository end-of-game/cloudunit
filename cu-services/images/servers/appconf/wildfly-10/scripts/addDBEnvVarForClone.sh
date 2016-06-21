#!/bin/bash

set -x

export ENV_FILE=/etc/environment

sed -i -e 's/^CU_DATABASE_USER_'$4'=.*$/CU_DATABASE_USER_'$4'='$1'/g' $ENV_FILE
sed -i -e 's/^CU_DATABASE_PASSWORD_'$4'=.*$/CU_DATABASE_PASSWORD_'$4'='$2'/g' $ENV_FILE
sed -i -e 's/^CU_DATABASE_DNS_'$4'=.*$/CU_DATABASE_DNS_'$4'='$3'/g' $ENV_FILE

