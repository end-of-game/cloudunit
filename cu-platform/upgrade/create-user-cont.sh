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
