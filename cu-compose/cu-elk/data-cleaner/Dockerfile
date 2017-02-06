FROM ubuntu:latest
MAINTAINER s.musso@treeptik.fr

COPY ./data-cleaner /
COPY curator /etc/curator
ADD curator/cron /etc/cron.d/curator

RUN apt update && apt install -y python python-pip cron \
    && pip install elasticsearch-curator \
    && crontab -u root /etc/cron.d/curator

ADD curator/cron /etc/cron.d/curator

LABEL origin cloudunit-monitoring
LABEL application-type monitoring
LABEL application-version 0.1

CMD ["/data-cleaner"]
