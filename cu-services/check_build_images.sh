#!/bin/bash

images_list_expected=()

images_list_expected+=('cloudunit/wildfly-9')
images_list_expected+=('cloudunit/wildfly-8')
images_list_expected+=('cloudunit/wildfly-10')
images_list_expected+=('cloudunit/redis-3-2')
images_list_expected+=('cloudunit/mysql-5-7')
images_list_expected+=('cloudunit/mysql-5-6')
images_list_expected+=('cloudunit/mysql-5-5')
images_list_expected+=('cloudunit/mongo-2-6')
images_list_expected+=('cloudunit/postgis-2-2')
images_list_expected+=('cloudunit/postgresql-9-5')
images_list_expected+=('cloudunit/postgresql-9-4')
images_list_expected+=('cloudunit/postgresql-9-3')
images_list_expected+=('cloudunit/apache-2-2')
images_list_expected+=('cloudunit/tomcat-9')
images_list_expected+=('cloudunit/tomcat-85')
images_list_expected+=('cloudunit/tomcat-8')
images_list_expected+=('cloudunit/tomcat-7')
images_list_expected+=('cloudunit/tomcat-6')
images_list_expected+=('cloudunit/base-jessie')
images_list_expected+=('cloudunit/base-16.04')
images_list_expected+=('cloudunit/base-14.04')
images_list_expected+=('cloudunit/base-12.04')
images_list_expected+=('cloudunit/elasticsearch-2.4')
images_list_expected+=('cloudunit/rabbitmq-3.6')
images_list_expected+=('cloudunit/activemq-5.13')
images_list_expected+=('cloudunit/fatjar')
images_list_expected+=('cloudunit/nginx-1.10')

# Function to test if a value exist in array
in_array() {
    local img_list=${1}[@]
    local entry=${2}
    for i in ${!img_list}; do
        if [[ ${i} == ${entry} ]]; then
            return 0
        fi
    done
    return 1
}

# Store the "docker images" result inside array
mapfile -t images_list_found < <(docker images | grep cloudunit)

# Intialize an array to store the list of images not found
images_list_not_found=()

# Loop to test if each entry of images_list_expected array is in the images_list_found array
for img in "${images_list_expected[@]}"
do
   in_array images_list_found $img || images_list_not_found+=($img)
done

red=`tput setaf 1`
green=`tput setaf 2`
reset=`tput sgr0`

# Print the test result
if [ ${#images_list_not_found[@]} -gt 0 ]; then
        echo "${red}"
        echo "Build test of Cloudunit Docker images"
        echo "-------------------------------------"
        echo "Test KO"
        echo "These images are missing:"
        printf '%s\n' "${images_list_not_found[@]}"
        echo "${reset}"
        exit 1
else
        echo "${green}"
        echo "Build test of Cloudunit Docker images"
        echo "-------------------------------------"
        echo "Test OK - All Cloudunit images are built"
        echo "${reset}"
        exit 0
fi

