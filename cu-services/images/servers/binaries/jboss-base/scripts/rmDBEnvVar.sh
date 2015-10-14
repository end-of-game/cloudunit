#!/bin/sh

export env_file=/etc/environment

sed -i "/CU_DATABASE_USER/d" $env_file
sed -i "/CU_DATABASE_PASSWORD/d" $env_file
sed -i "/CU_DATABASE_DNS/d" $env_file
