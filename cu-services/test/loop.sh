#!/bin/bash

trap 'exit 42' SIGTERM

/etc/init.d/ssh start
/tomcat.sh

echo "lol" > /tomcat.txt

# while true; do :; done 
# tailf /tomcat.txt &

# tailf /tomcat.txta

# wait indefinetely
while true
do
      tail -f /dev/null & wait ${!}
done

