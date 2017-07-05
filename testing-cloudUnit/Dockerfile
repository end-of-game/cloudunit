#Instruction initializes a new build stage and sets the Base Image for subsequent instructions
FROM node:6.11

#Indication person to contact
MAINTAINER Ismaël Charifou <contact@treeptik.fr>

#Create a folder where we will work
RUN mkdir /testing-cloudUnit

#Redirect to folder
WORKDIR /testing-cloudUnit

#Run updates and install deps
RUN apt-get update

#Run command install nodemon
RUN npm install nodemon -g

#Copy a package.json to the file to build
COPY package.json /testing-cloudUnit/package.json

#Run to install npm
RUN npm install

#Copy
COPY index.js /testing-cloudUnit

#Port allocation
ENV PORT 3000

#Opening port to external
EXPOSE $PORT

#Command to execute when build is completed
CMD ["nodemon", "index.js"]

