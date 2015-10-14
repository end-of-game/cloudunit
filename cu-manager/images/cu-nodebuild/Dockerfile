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

FROM cloudunit/nodebuildbase

ENV BUILDER_UID=builder_uid
ENV BUILDER_GID=builder_gid

# Création de l'utilisateur builder
RUN groupadd -r --gid $BUILDER_GID builder && useradd -m -r --uid $BUILDER_UID -g builder builder
USER builder

# Fix pour permettre de tirer correctement certaines dépendances
RUN git config --global url."https://".insteadOf git://

WORKDIR /home/builder/app

CMD ./build.sh
