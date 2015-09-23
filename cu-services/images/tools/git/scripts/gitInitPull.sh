#!/bin/sh

export GIT_DIR=$CU_GIT_HOME/.git

cd /cloudunit/git/.git
echo "$PWD"
touch .gitignore
git --git-dir=$GIT_DIR --work-tree=$CU_GIT_HOME add .
git --git-dir=$GIT_DIR --work-tree=$CU_GIT_HOME commit -m "initial cloudunit commit"

git --git-dir=$GIT_DIR --work-tree=$CU_GIT_HOME pull -v $1


git config core.bare true

#NEED?
sleep 5

chown -R $USER:$USER $CU_GIT_HOME
chown -R $USER:$USER $TOMCAT_HOME
