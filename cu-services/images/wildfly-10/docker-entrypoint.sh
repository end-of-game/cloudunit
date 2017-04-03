#!/bin/bash

if [[ ! -z "$WILDFLY_USER" ]] && [[ ! -z "$WILDFLY_PASSWORD" ]]
then
    add-user.sh -s $WILDFLY_USER $WILDFLY_PASSWORD
fi

exec "$@"
