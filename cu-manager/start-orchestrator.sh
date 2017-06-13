#! /bin/bash
mvn spring-boot:run -Plocal -pl cu-docker-orchestrator
PID=$!
echo $PID > .pid

for i in {1..60}
do
    STATUS=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8081)
    if [ $STATUS -eq 200 ]; then
        exit 0
    fi
    sleep 1
done
exit 1
