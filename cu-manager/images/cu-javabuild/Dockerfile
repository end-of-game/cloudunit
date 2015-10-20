# LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
# but CloudUnit is licensed too under a standard commercial license.
# Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
# If you are not sure whether the GPL is right for you,
# you can always test our software under the GPL and inspect the source code before you contact us
# about purchasing a commercial license.

# LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
# or promote products derived from this project without prior written permission from Treeptik.
# Products or services derived from this software may not be called "CloudUnit"
# nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
# For any questions, contact us : contact@treeptik.fr

FROM buildpack-deps:jessie-curl

RUN mkdir /cloudunit/java && wget https://github.com/Treeptik/cloudunit/releases/download/1.0/jdk-8u25-linux-x64.tar.gz -O /tmp/jdk-8u25-linux-x64.tar.gz \
        && tar -xvf /tmp/jdk-8u25-linux-x64.tar.gz -C /cloudunit/java && rm /tmp/jdk-* \
	&& wget https://github.com/Treeptik/CloudUnit/releases/download/1.0/apache-maven-3.3.3-bin.tar.gz -O /tmp/apache-maven-3.3.3-bin.tar.gz \
	&& tar -xvf /tmp/apache-maven-3.3.3-bin.tar.gz -C /usr/share && rm /tmp/apache-* \
	&& mv /usr/share/apache-maven-3.3.3 /usr/share/maven ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV JAVA_HOME /cloudunit/java/jdk1.8.0_25
ENV BUILDER_UID=builder_uid
ENV BUILDER_GID=builder_gid

# Création de l'utilisateur builder
RUN groupadd -r --gid $BUILDER_GID builder && useradd -m -r --uid $BUILDER_UID -g builder builder
USER builder

WORKDIR /home/builder/app
