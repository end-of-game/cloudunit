#! /bin/bash -e

# if `docker run` first argument start with `--` the user is passing fatjar launcher arguments
if [[ $# -lt 1 ]] || [[ "$1" == "--"* ]]; then
  file=`ls /opt/cloudunit/tmp`
  if [ -f "$file" ]
  then
	echo "$file found."
	mv /opt/cloudunit/tmp/$file /opt/cloudunit/fatjar/boot.jar
  fi
  eval "exec java -jar /opt/cloudunit/fatjar/boot.jar  \"\$@\""

# As argument is not fatjar, assume user want to run his own process, for sample a `bash` shell to explore this image
exec "$@"