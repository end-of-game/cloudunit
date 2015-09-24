#!/bin/bash

ssh-copy-id -i $CU_USER_HOME/.ssh/id_rsa.pub $CU_USER@$CU_SERVERS_IP
chown -R $CU_USER:$CU_USER $CU_USER_HOME/.ssh

