#!/usr/bin/env bash

source /etc/environment

[ -z "$MYSQL_ROOT_PASSWORD" ] && echo "Need to set MYSQL_ROOT_PASSWORD in /etc/environment. Exit in error" && exit 1;
[ -z "$CU_PORTAL_DOMAIN" ] && echo "Need to set CU_PORTAL_DOMAIN in /etc/environment. Exit in error" && exit 1;
[ -z "$CU_MANAGER_DOMAIN" ] && echo "Need to set CU_MANAGER_DOMAIN in /etc/environment. Exit in error" && exit 1;
[ -z "$CU_GITLAB_DOMAIN" ] && echo "Need to set CU_GITLAB_DOMAIN in /etc/environment. Exit in error" && exit 1;
[ -z "$CU_JENKINS_DOMAIN" ] && echo "Need to set CU_JENKINS_DOMAIN in /etc/environment. Exit in error" && exit 1;
[ -z "$CU_KIBANA_DOMAIN" ] && echo "Need to set CU_KIBANA_DOMAIN in /etc/environment. Exit in error" && exit 1;

echo ""
echo "CU_PORTAL_DOMAIN="$CU_PORTAL_DOMAIN
echo "CU_MANAGER_DOMAIN="$CU_MANAGER_DOMAIN
echo "CU_GITLAB_DOMAIN="$CU_GITLAB_DOMAIN
echo "CU_JENKINS_DOMAIN="$CU_JENKINS_DOMAIN
echo "CU_KIBANA_DOMAIN="$CU_KIBANA_DOMAIN
echo ""

cp -f nginx/sites-enabled/cloudunit.conf.template nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_PORTAL_DOMAIN/$CU_PORTAL_DOMAIN/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_MANAGER_DOMAIN/$CU_MANAGER_DOMAIN/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_JENKINS_DOMAIN/$CU_JENKINS_DOMAIN/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_GITLAB_DOMAIN/$CU_GITLAB_DOMAIN/g" nginx/sites-enabled/cloudunit.conf
sed -i "s/CU_KIBANA_DOMAIN/$CU_KIBANA_DOMAIN/g" nginx/sites-enabled/cloudunit.conf
echo "File 'nginx/sites-enabled/cloudunit.conf' configured"

cp -f nginx/www/portal/index.html.template nginx/www/portal/index.html
sed -i "s/CU_PORTAL_DOMAIN/$CU_PORTAL_DOMAIN/g" nginx/www/portal/index.html
sed -i "s/CU_MANAGER_DOMAIN/$CU_MANAGER_DOMAIN/g" nginx/www/portal/index.html
sed -i "s/CU_JENKINS_DOMAIN/$CU_JENKINS_DOMAIN/g" nginx/www/portal/index.html
sed -i "s/CU_GITLAB_DOMAIN/$CU_GITLAB_DOMAIN/g" nginx/www/portal/index.html
sed -i "s/CU_KIBANA_DOMAIN/$CU_KIBANA_DOMAIN/g" nginx/www/portal/index.html
echo "File 'nginx/www/portal/index.html' configured"
echo ""
