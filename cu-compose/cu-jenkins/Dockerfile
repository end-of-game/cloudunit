FROM jenkins:2.46.1

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

RUN add-apt-repository -y "deb http://ppa.launchpad.net/natecarlson/maven3/ubuntu precise main"
RUN apt-get update
RUN apt-get install maven3 -y
RUN ln -s /usr/bin/mvn3 /usr/bin/mvn
RUN update-java-alternatives -s java-1.8.0-openjdk-amd64

RUN mkdir /root/.m2
ADD settings.xml /root/.m2/settings.xml

RUN rm -rf /var/lib/apt/lists/*

USER jenkins


