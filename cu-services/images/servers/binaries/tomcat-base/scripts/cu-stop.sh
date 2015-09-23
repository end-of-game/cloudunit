export CATALINA_HOME="/cloudunit/binaries"
export CATALINA_BASE="/cloudunit/appconf"

export JAVA_HOME=$1
sh $CATALINA_HOME/bin/catalina.sh stop
