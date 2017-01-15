FROM buildpack-deps:jessie

RUN groupadd --gid 1000 node \
  && useradd --uid 1000 --gid node --shell /bin/bash --create-home node

## CLOUDUNIT :: BEGINNING
ENV CU_BACKUP /opt/cloudunit/backup
ENV CU_HOOKS /opt/cloudunit/hooks
ENV CU_JAVA /opt/cloudunit/java
ENV CU_SCRIPTS /opt/cloudunit/scripts
ENV CU_SHARED /opt/cloudunit/shared
ENV CU_TMP /opt/cloudunit/tmp

ENV CU_SERVER_RESTART_POST_DEPLOYMENT false
ENV CU_SOFTWARE ""
ENV CU_SERVER_MANAGER_PATH ""
ENV CU_SERVER_MANAGER_PORT 80
ENV CU_SERVER_PORT 80
ENV CU_SERVER_RESTART_POST_CREDENTIALS true
ENV CU_DEFAULT_LOG_FILE access.log
ENV CU_LOGS STDOUT
ENV JAVA_OPTS " "

# add custom scripts
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/*
## CLOUDUNIT :: END

# gpg keys listed at https://github.com/nodejs/node
RUN set -ex \
  && for key in \
    9554F04D7259F04124DE6B476D5A82AC7E37093B \
    94AE36675C464D64BAFA68DD7434390BDBE9B9C5 \
    0034A06D9D9B0064CE8ADF6BF1747F4AD2306D93 \
    FD3A5288F042B6850C66B31F09FE44734EB7990E \
    71DCFD284A79C3B38668286BC97EC7A07EDE3FC1 \
    DD8F2338BAE7501E3DD5AC78C273792F7D83545D \
    B9AE9905FFD7803F25714661B63B535A4C206CA9 \
    C4F0DFFF4E8C1A8236409D08E73BC641CC11F4C8 \
  ; do \
    gpg --keyserver ha.pool.sks-keyservers.net --recv-keys "$key"; \
  done

ENV NPM_CONFIG_LOGLEVEL info
ENV NODE_VERSION 7.4.0

RUN curl -SLO "https://nodejs.org/dist/v$NODE_VERSION/node-v$NODE_VERSION-linux-x64.tar.xz" \
  && curl -SLO "https://nodejs.org/dist/v$NODE_VERSION/SHASUMS256.txt.asc" \
  && gpg --batch --decrypt --output SHASUMS256.txt SHASUMS256.txt.asc \
  && grep " node-v$NODE_VERSION-linux-x64.tar.xz\$" SHASUMS256.txt | sha256sum -c - \
  && tar -xJf "node-v$NODE_VERSION-linux-x64.tar.xz" -C /usr/local --strip-components=1 \
  && rm "node-v$NODE_VERSION-linux-x64.tar.xz" SHASUMS256.txt.asc SHASUMS256.txt \
  && ln -s /usr/local/bin/node /usr/local/bin/nodejs \
  && npm install express --save

ADD index.js /index.js
ADD docker-entrypoint.sh /docker-entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]

LABEL origin application
LABEL application-type node
LABEL application-version 7-4-0
