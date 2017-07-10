#Instruction initializes a new build stage and sets the Base Image for subsequent instructions
FROM node:latest

#Indication person to contact
MAINTAINER Ismaël Charifou <contact@treeptik.fr>

#Create a directory where we will work
RUN mkdir /testing-cloudunit

#Redirect to folder
WORKDIR /testing-cloudunit

#Run updates and install deps
RUN apt-get update

#Run command install nodemon
RUN npm install nodemon -g

#Copy a package.json to the file to build
COPY package.json /testing-cloudunit/package.json

#Run to install npm
RUN npm install

#Run to install mongodb
#RUN npm install mongodb

#Copycd te
COPY index.js /testing-cloudunit

#Port allocation
#ENV PORT 3000

#Opening port to external
EXPOSE 3000

#Command to execute when build is completed
CMD ["nodemon", "index.js"]
