#Instruction initializes a new build stage and sets the Base Image for subsequent instructions
FROM node:latest

#Indication person to contact
MAINTAINER Ismaël Charifou <contact@treeptik.fr>
LABEL MAINTAINER="Ismaël Charifou <contact@treeptik.fr>"

#Create a directory where we will work
RUN mkdir /testing-cloudunit

#Redirect to folder
WORKDIR /testing-cloudunit

RUN npm install nodemon -g

#Copy a package.json to the file to build
COPY package.json /testing-cloudunit/package.json

#Run to install npm
RUN npm install

#Copycd te
COPY index.js /testing-cloudunit

#Opening port to external
EXPOSE 3000

#Command to execute when build is completed
CMD ["nodemon", "index.js"]
