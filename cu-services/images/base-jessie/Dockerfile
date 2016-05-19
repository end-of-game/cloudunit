FROM debian:jessie

# Install default software
RUN apt-get update && apt-get install -y vim \
                                         openssh-server \
                                         curl \
                                         wget \
                                         netcat

# Create directory for CloudUnit
RUN mkdir -p /cloudunit/java && \
    mkdir -p /cloudunit/scripts && \
    mkdir -p /cloudunit/binaries && \
    mkdir -p /cloudunit/tmp && \
    mkdir -p /cloudunit/tools && \
    mkdir -p /cloudunit/appconf && \
    mkdir -p /cloudunit/appconf/logs && \
    mkdir -p /cloudunit/software && \
    mkdir -p /cloudunit/appconf/hooks/ && \
    mkdir -p /cloudunit/backup &&  \
    mkdir -p /cloudunit/shared &&  \
    mkdir -p /var/run/sshd

# Environment variables
ENV CU_LOGS /cloudunit/appconf/logs
ENV CU_JAVA /cloudunit/java
ENV CU_SCRIPTS /cloudunit/scripts
ENV CU_USER_HOME /cloudunit/home
ENV CU_HOOKS /cloudunit/appconf/hooks
ENV CU_SHARED /cloudunit/shared

RUN echo -n "CU_JAVA=$CU_JAVA\nCU_SCRIPTS=$CU_SCRIPTS\nCU_USER_HOME=$CU_USER_HOME\nCU_LOGS=$CU_LOGS\nCU_HOOKS=$CU_HOOKS\n" >> /etc/environment

COPY sshd_config /etc/ssh/sshd_config
COPY hooks /cloudunit/appconf/hooks
RUN chmod a+x /cloudunit/appconf/hooks/*

LABEL origin cloudunit

# CloudUnit agent for database sync status
RUN wget https://github.com/Treeptik/cloudunit/releases/download/1.0/cloudunitAgent-1.0-SNAPSHOT.jar -O /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar









