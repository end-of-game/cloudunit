#Instruction initializes a new build stage and sets the Base Image for subsequent instructions
FROM node:6.11

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

EXPOSE 5000

CMD ["npm", "start"]

