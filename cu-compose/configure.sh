#!/usr/bin/env bash

source /etc/environment

[ -z "$CU_PORTAL_URL" ] && echo "Need to set CU_PORTAL_URL in /etc/environment" && exit 1;
[ -z "$CU_MANAGER_URL" ] && echo "Need to set CU_MANAGER_URL in /etc/environment" && exit 1;
[ -z "$CU_GITLAB_URL" ] && echo "Need to set CU_GITLAB_URL in /etc/environment" && exit 1;
[ -z "$CU_JENKINS_URL" ] && echo "Need to set CU_JENKINS_URL in /etc/environment" && exit 1;
[ -z "$CU_KIBANA_URL" ] && echo "Need to set CU_KIBANA_URL in /etc/environment" && exit 1;

echo ""
echo "CU_PORTAL_URL="$CU_PORTAL_URL
echo "CU_MANAGER_URL="$CU_MANAGER_URL
echo "CU_GITLAB_URL="$CU_GITLAB_URL
echo "CU_JENKINS_URL="$CU_JENKINS_URL
echo "CU_KIBANA_URL="$CU_KIBANA_URL
echo ""

cp -f nginx/sites-enabled/cloudunit.conf.template nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_PORTAL_URL/$CU_PORTAL_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_MANAGER_URL/$CU_MANAGER_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_JENKINS_URL/$CU_JENKINS_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_GITLAB_URL/$CU_GITLAB_URL/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_KIBANA_URL/$CU_KIBANA_URL/g" nginx/sites-enabled/cloudunit.conf
echo "File 'nginx/sites-enabled/cloudunit.conf' configured"

cp -f nginx/www/portal/index.html.template nginx/www/portal/index.html
sed -i "s/CU_PORTAL_URL/$CU_PORTAL_URL/g" nginx/www/portal/index.html
sed -i "s/CU_MANAGER_URL/$CU_MANAGER_URL/g" nginx/www/portal/index.html
sed -i "s/CU_JENKINS_URL/$CU_JENKINS_URL/g" nginx/www/portal/index.html
sed -i "s/CU_GITLAB_URL/$CU_GITLAB_URL/g" nginx/www/portal/index.html
sed -i "s/CU_KIBANA_URL/$CU_KIBANA_URL/g" nginx/www/portal/index.html
echo "File 'nginx/www/portal/index.html' configured"
echo ""
