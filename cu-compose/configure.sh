#!/usr/bin/env bash

source /etc/environment

echo "CU_PORTAL_URL="$CU_PORTAL_URL
echo "CU_MANAGER_URL="$CU_MANAGER_URL
echo "CU_GITLAB_URL="$CU_GITLAB_URL
echo "CU_JENKINS_URL="$CU_JENKINS_URL
echo "CU_KIBANA_URL="$CU_KIBANA_URL

cp -f nginx/sites-enabled/cloudunit.conf.template nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_PORTAL_URL/$CU_PORTAL_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_MANAGER_URL/$CU_MANAGER_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_JENKINS_URL/$CU_JENKINS_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_GITLAB_URL/$CU_GITLAB_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_KIBANA_URL/$CU_KIBANA_URL/g" nginx/sites-enabled/cloudunit.conf

cp -f nginx/www/portal/index.html.template nginx/www/portal/index.html
sed -i "s/CU_PORTAL_URL/$CU_PORTAL_URL/g" nginx/www/portal/index.html
sed -i "s/CU_MANAGER_URL/$CU_MANAGER_URL/g" nginx/www/portal/index.html
sed -i "s/CU_JENKINS_URL/$CU_JENKINS_URL/g" nginx/www/portal/index.html
sed -i "s/CU_GITLAB_URL/$CU_GITLAB_URL/g" nginx/www/portal/index.html
sed -i "s/CU_KIBANA_URL/$CU_KIBANA_URL/g" nginx/www/portal/index.html

