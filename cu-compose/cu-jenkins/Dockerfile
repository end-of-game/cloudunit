FROM jenkins:2.19.3

USER root

RUN apt-get update \
	&& apt-get install --no-install-recommends --no-install-suggests -y \
                        curl \
						ca-certificates \
						software-properties-common python-software-properties \
						vim

# Add nodesource PPA for specific version of node and install
RUN curl -sL https://deb.nodesource.com/setup_5.x | bash
RUN apt-get update && apt-get install -y nodejs

# Fix npm
RUN cd /usr/lib/node_modules/npm \
  && npm install fs-extra \
  && sed -i -e s/graceful-fs/fs-extra/ -e s/fs\.move/fs.rename/ ./lib/utils/rename.js

RUN npm install -g bower gulp

RUN apt-get install maven -y
RUN update-java-alternatives -s java-1.8.0-openjdk-amd64

RUN rm -rf /var/lib/apt/lists/*

USER jenkins


