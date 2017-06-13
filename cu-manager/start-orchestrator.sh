#! /bin/bash
mvn spring-boot:run -Plocal -pl cu-docker-orchestrator > /dev/null 2>&1 &
PID=$!
echo $PID > .pid

for i in {1..30}
do
    STATUS=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8081)
    if [ $STATUS -eq 200 ]; then
        exit 0
    fi
    sleep 1
done
exit 1
