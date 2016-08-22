/opt/cloudunit/wildfly/bin/jboss-cli.sh
connect
data-source add  --name=$1 --driver-name=$2 --connection-url=$3 --jndi-name=jboss/jdbc/$1
data-source enable --name=$1 
