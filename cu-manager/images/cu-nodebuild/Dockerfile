# LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

FROM ubuntu:14.04

ENV BUILDER_UID=builder_uid
ENV BUILDER_GID=builder_gid

# Dépendances Debian
RUN apt-get update && apt-get install -y\
    ruby-full\
    nodejs\
    nodejs-legacy\
    git\
    npm

# Dépendances Ruby
RUN gem install sass compass

# Dépendances Node.js
RUN npm install -g grunt grunt-cli bower

# Passage du système en UTF-8
RUN echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen
RUN locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN apt-get install -y git-core

# Création de l'utilisateur builder
RUN groupadd -r --gid $BUILDER_GID builder && useradd -m -r --uid $BUILDER_UID -g builder builder
USER builder

# Fix pour permettre de tirer correctement certaines dépendances
RUN git config --global url."https://".insteadOf git://

WORKDIR /home/builder/app

CMD ./build.sh
