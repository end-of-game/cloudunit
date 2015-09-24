#!/bin/bash

source /home/admincu/.profile

while read line  
do  
	NAME="echo $line | cut -d'@' -f 1" 
	NAME_WO_OLD="eval $NAME | sed "s/-old$CU_UPGRADE//""
	NAME_WO_DATA_OLD="eval $NAME | sed "s/-data-old$CU_UPGRADE//""
	ARGS="echo $line | cut -d'@' -f 2"

	case "$line" in
		*tomcat-8*)
			echo "tomcat-8 present in $(eval $NAME)"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) --volumes-from tomcat-8 --volumes-from java $(eval $NAME)
			;;
		*tomcat-7*)
			echo "tomcat-7 present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) --volumes-from tomcat-7 --volumes-from java $(eval $NAME)
			;;
		*tomcat-6*)
			echo "tomcat-6 present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) --volumes-from tomcat-6 --volumes-from java $(eval $NAME)
			;;
		*jboss-8*)
			echo "jboss-8 present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) --volumes-from jboss-8 --volumes-from java $(eval $NAME)
			;;
		*jboss-7*)
			echo "jboss-7 present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) --volumes-from jboss-7 --volumes-from java $(eval $NAME)
			;;
		*mysql*)
			echo "mysql present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) $(eval $NAME)
			docker create --name $(eval $NAME_WO_DATA_OLD) --volumes-from $(eval $NAME_WO_OLD) cloudunit/mysql-5-5 $(eval $ARGS)
			;;
		*mongo*)
			echo "mongo present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) $(eval $NAME)
			docker create --name $(eval $NAME_WO_DATA_OLD) --volumes-from $(eval $NAME_WO_OLD) cloudunit/mongo-2-6 $(eval $ARGS)
			;;
		*redis*)
			echo "redis present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) $(eval $NAME)
			docker create --name $(eval $NAME_WO_DATA_OLD) --volumes-from $(eval $NAME_WO_OLD) cloudunit/redis-3-0 $(eval $ARGS)
			;;
		*postgresql*)
			echo "postgresql present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) $(eval $NAME)
			docker create --name $(eval $NAME_WO_DATA_OLD) --volumes-from $(eval $NAME_WO_OLD) cloudunit/postgresql-9-3 $(eval $ARGS)
			;;
		*git*)
			echo "git present in $line"
			docker create --label=upgrade=commit --name $(eval $NAME_WO_OLD) $(eval $NAME)
			;;

		*)
			echo "not present" ;;
	esac
done < images_from_commit

mv images_from_commit images_from_commit.bak
