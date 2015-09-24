#!/bin/sh

export env_file=/etc/environment

sed -i "/CU_DATABASE_USER_$1/d" $env_file 
sed -i "/CU_DATABASE_PASSWORD_$1/d" $env_file
sed -i "/CU_DATABASE_DNS_$1/d" $env_file
