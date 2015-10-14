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

#!/bin/bash
# On récupère $CU_UPGRADE
source /home/admincu/.profile
if [ $CU_UPGRADE == 1 ]; then
	CONT_FOR_COMMIT="docker ps -a | egrep -v 'cuplatform_|cloudunit/jboss-8|cloudunit/jboss-7|cloudunit/tomcat-8|cloudunit/tomcat-7|cloudunit/tomcat-6|cloudunit/java|CONTAINER' | grep 'cloudunit/mysql-5-5-data\|cloudunit/mongo-2-6-data\|cloudunit/redis-3-0-data\|cloudunit/postgresql-9-3-data\|cloudunit/jboss-appconf\|cloudunit/tomcat-appconf\|cloudunit/git' | cut -d' ' -f 1"
else
	CONT_FOR_COMMIT="docker ps -a --filter 'label=upgrade=commit' | egrep -v 'CONTAINER' | cut -d' ' -f 1"
fi

eval $CONT_FOR_COMMIT > cont_for_commit

if [ -f images_from_commit ]; then
	echo Suppression du fichier images_from_commit
	rm images_from_commit
fi

while read line  
do   
	IMAGE="echo $(docker inspect --format {{.Name}} $line |sed 's/\///')-old$CU_UPGRADE | tr '[:upper:]' '[:lower:]'"
	IMAGE_WO_DATA="docker inspect --format {{.Name}} $line | sed 's/\///' | sed 's/-data//'"

	case "$IMAGE" in
		*mysql-5-5*)

			echo -ne "$(eval $IMAGE) @" >> images_from_commit

			echo $(eval $IMAGE_WO_DATA)
			docker inspect --format {{.Config.Cmd}} $(eval $IMAGE_WO_DATA) | cut -d "[" -f 2 | cut -d "]" -f 1 >> images_from_commit
			;;

		*mongo-2-6*)

			echo -ne "$(eval $IMAGE) @" >> images_from_commit

			echo $(eval $IMAGE_WO_DATA)
			docker inspect --format {{.Config.Cmd}} $(eval $IMAGE_WO_DATA) | cut -d "[" -f 2 | cut -d "]" -f 1 >> images_from_commit
			;;

		*redis-3-0*)

			echo -ne "$(eval $IMAGE) @" >> images_from_commit

			echo $(eval $IMAGE_WO_DATA)
			docker inspect --format {{.Config.Cmd}} $(eval $IMAGE_WO_DATA) | cut -d "[" -f 2 | cut -d "]" -f 1 >> images_from_commit
			;;

		*postgresql-9-3*)

			echo -ne "$(eval $IMAGE) @" >> images_from_commit

			echo $(eval $IMAGE_WO_DATA)
			docker inspect --format {{.Config.Cmd}} $(eval $IMAGE_WO_DATA) | cut -d "[" -f 2 | cut -d "]" -f 1 >> images_from_commit
			;;

		*)

			eval $IMAGE >> images_from_commit
	esac
	docker commit $line $(eval $IMAGE)

done < cont_for_commit

rm cont_for_commit
