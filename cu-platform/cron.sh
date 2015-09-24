#!/bin/bash

sed 's|docker-compose |/usr/local/bin/docker-compose |g' /home/admincu/cloudunit/cu-platform/start-platform.sh > /home/admincu/cloudunit/cu-platform/start-platform-cron.sh && chmod +x /home/admincu/cloudunit/cu-platform/start-platform-cron.sh
sed 's|sudo rm -rf /registry/\* /var/log/cloudunit && ||' /home/admincu/cloudunit/cu-platform/reset-all.sh | sed 's/start-platform/start-platform-cron/' > /home/admincu/cloudunit/cu-platform/reset-all-cron.sh && chmod +x /home/admincu/cloudunit/cu-platform/reset-all-cron.sh
/home/admincu/cloudunit/cu-platform/reset-all-cron.sh -y > /home/admincu/reset-cron-log
