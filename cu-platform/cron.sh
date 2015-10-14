# LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
# but CloudUnit is licensed too under a standard commercial license.
# Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
# If you are not sure whether the GPL is right for you,
# you can always test our software under the GPL and inspect the source code before you contact us
# about purchasing a commercial license.

# LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
# or promote products derived from this project without prior written permission from Treeptik.
# Products or services derived from this software may not be called "CloudUnit"
# nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
# For any questions, contact us : contact@treeptik.fr

#!/bin/bash

sed 's|docker-compose |/usr/local/bin/docker-compose |g' /home/admincu/cloudunit/cu-platform/start-platform.sh > /home/admincu/cloudunit/cu-platform/start-platform-cron.sh && chmod +x /home/admincu/cloudunit/cu-platform/start-platform-cron.sh
sed 's|sudo rm -rf /registry/\* /var/log/cloudunit && ||' /home/admincu/cloudunit/cu-platform/reset-all.sh | sed 's/start-platform/start-platform-cron/' > /home/admincu/cloudunit/cu-platform/reset-all-cron.sh && chmod +x /home/admincu/cloudunit/cu-platform/reset-all-cron.sh
/home/admincu/cloudunit/cu-platform/reset-all-cron.sh -y > /home/admincu/reset-cron-log
