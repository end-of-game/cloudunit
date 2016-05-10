#!/usr/bin/env bash

su -l redis -c "tar cvf /cloudunit/backup/data-db.tar /cloudunit/database"
