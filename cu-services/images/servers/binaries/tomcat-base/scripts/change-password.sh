#!/bin/sh

set -x

echo "$1:$2" | chpasswd
sed -i -e 's/CU_PASSWORD='$CU_PASSWORD'/CU_PASSWORD='$2'/g' /etc/environment
