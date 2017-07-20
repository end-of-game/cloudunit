#!/bin/bash

echo "---------------------------------------------------------------------------------"
echo " Automatic vulnerability scanner for CloudUnit images"
echo "---------------------------------------------------------------------------------"
# get all docker images name
name=$(docker images | grep cloudunit | awk 'BEGIN { OFS = ":" }{ print $1, $2 }')
ID=$(docker images | grep cloudunit | awk 'BEGIN { OFS = ":" }{ print $3 }')
subName=$(docker images | grep cloudunit | awk '{ print substr($1,11) }')

echo "CloudUnit images: $NAME"

#echo "formating CloudUnit images $subName"
SAVEIFS=$IFS

# Change IFS to new line
IFS=$'\n'
names=($subName)
ids=($ID)

# Restore IFS
IFS=$SAVEIFS
for (( i=0; i<${#names[@]}; i++ ))
do
   docker tag ${ids[$i]} ${names[$i]}
done

# launch clairctl push, pull, analyze, report to html to docker registry with debug mode 
# to hide debug mode, remove --log-level debug 
for arg in $subName ; do
    echo "clairctl push -l $arg --config config.yml"
	clairctl push -l $arg --config config.yml
    echo ""
    echo "clairctl pull -l $arg  --config config.yml"
    clairctl pull -l $arg  --config config.yml
    echo ""
    echo "clairctl analyze -l $arg  --config config.yml --log-level debug"
    clairctl analyze -l $arg  --config config.yml --log-level debug
    echo ""
    echo "clairctl report -l $arg --config config.yml --log-level debug"
	clairctl report -l $arg --config config.yml --log-level debug
done

chown $(whoami) -R CU_reports
echo "---------------------------------------------------------------------------------"
echo "> Results: ./CU_reports/html"
echo "---------------------------------------------------------------------------------"
exit 0