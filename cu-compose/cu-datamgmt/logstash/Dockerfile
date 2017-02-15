FROM logstash:5.2

HEALTHCHECK CMD curl --fail http://localhost:9600/ || exit 1

COPY conf.d /etc/logstash/conf.d

LABEL origin cloudunit-monitoring
LABEL application-type logstash
LABEL application-version 5.1.2
