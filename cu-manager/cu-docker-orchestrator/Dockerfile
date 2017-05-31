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

FROM maven:3.5.0-jdk-8

# git branch being bundled in this docker image
ARG GIT_BRANCH
ENV GIT_BRANCH ${GIT_BRANCH:-dev-3.x}
RUN cd /opt && git clone https://github.com/Treeptik/CloudUnit.git -b ${GIT_BRANCH} --depth 1 cloudunit

WORKDIR /opt/cloudunit

# Build Maven with profiles
RUN mvn clean install -DskipTests

EXPOSE 8080

COPY docker-entrypoint.sh /bin

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["start"]
