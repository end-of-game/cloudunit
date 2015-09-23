FROM maven:3-jdk-8

ENV BUILDER_UID=builder_uid
ENV BUILDER_GID=builder_gid

# Création de l'utilisateur builder
RUN groupadd -r --gid $BUILDER_GID builder && useradd -m -r --uid $BUILDER_UID -g builder builder
USER builder
RUN wget https://github.com/Treeptik/CloudUnit/releases/download/0.9/m2.tar.gz -O /tmp/m2.tar.gz && tar -xvf /tmp/m2.tar.gz -C /home/builder && mv /home/builder/m2 /home/builder/.m2

WORKDIR /home/builder/app
