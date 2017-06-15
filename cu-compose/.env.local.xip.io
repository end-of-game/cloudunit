# Set CloudUnit deployment Environment

MYSQL_ROOT_PASSWORD=changeit

CU_DOMAIN=127.0.0.1.xip.io
CU_MANAGER_DOMAIN=admin.127.0.0.1.xip.io
CU_GITLAB_DOMAIN=gitlab.127.0.0.1.xip.io
CU_JENKINS_DOMAIN=jenkins.127.0.0.1.xip.io
CU_KIBANA_DOMAIN=kibana.127.0.0.1.xip.io
CU_LETSCHAT_DOMAIN=letschat.127.0.0.1.xip.io
CU_NEXUS_DOMAIN=nexus.127.0.0.1.xip.io
CU_SONAR_DOMAIN=sonar.127.0.0.1.xip.io
TZ=Europe/Paris

ELASTICSEARCH_URL=elasticsearch
MYSQL_DATABASE=cloudunit
HOSTNAME=$HOSTNAME

CU_COMPOSE_FILES="-f docker-compose.dev.yml"
