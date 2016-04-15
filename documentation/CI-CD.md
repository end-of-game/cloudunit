# OVERVIEW

![Jenkins2 Logo](img/plateforme-trio.png "Devops Plateforme")

# CONTEXT

At this day, CloudUnit provides Jenkins2 and GitLab-CE. 
We are working for a full distribution between all these products.
We want to have the same users and roles from CloudUnit to Jenkins2 and GitLab-CE.
CloudUnit will be the masterchief to admin the others products.

# INSTALLATION

## GitLab

Follow these steps during the first time to run and configure GitLab

Into the vagrantbox 
```
cd cloudunit/cu-platform
./gitlab.sh
````

Docker will pull the image 

````
Unable to find image 'gitlab/gitlab-ce:latest' locally
Pulling repository gitlab/gitlab-ce
b557757c1998: Download complete
426a0cc6d7b0: Download complete
23f6bd545c17: Download complete
e4082297a963: Download complete
06b50a41723d: Download complete
1880161e1eb6: Download complete
8ab98c7ec538: Download complete
245c703c3c85: Download complete
85585b72111d: Download complete
5efc762be96b: Download complete
bb93196f1588: Download complete
a34dcb070670: Download complete
d83d6f96580c: Download complete
8fbff104bcc5: Download complete
Status: Downloaded newer image for gitlab/gitlab-ce:latest
70a38bf4b8d9132c7dcdf1b319ee8572787e9127b48cb56d718ea1170fe10160
````

You can play this to verify if everything is good.

```
docker ps | grep gitlab
70a38bf4b8d9 gitlab-ce:latest ... 0.0.0.0:422->22/tcp, 0.0.0.0:480->80/tcp, 0.0.0.0:4443->443/tcp 
```

So you have a container **gitlab** exposing its ports at 
* SSH at 422
* WebApp HTTP at 480
* API HTTPS at 4443

To access the application, you can open the URL [http://192.168.50.4:480](http://192.168.50.4:480).
Choose a **root** password and note it preciously.
Create a new project.
Name it **helloworld**


![GitLab Choose Password](img/gitlab-choose-password.png "Choose Password")    

![GitLab Create new project](img/gitlab-create-new-project.png "Create new project")    

![GitLab Create project helloworld](img/gitlab-create-project-helloworld.png "Create project helloworld")    

To follow this example, we will clone a skeleton project example:

```
mkdir test-gitlab-jenkins2
cd test-gitlab-jenkins2
wget https://github.com/Treeptik/cloudunit/releases/download/1.0/skeleton-gitlab-jenkins2-helloworld.zip
unzip skeleton-gitlab-jenkins2-helloworld.zip
cd helloworld
git init
git remote add origin http://192.168.50.4:480/root/helloworld.git
git add .
git commit -am "First Commit"
git push -u origin master
```

This application is the most simple jee webapp with a single jsp.
The important file to analyze is **Jenkinsfile** because it allows the pipeline as code plugin into Jenkins2.

```
Stage "Create CU-Server"
node {
    checkout scm
    mvn 'clean package'
    sh "wget https://github.com/Treeptik/cloudunit/releases/download/1.0/CloudUnitCLI.jar -O /tmp/cloudunit-cli.jar"
    sh "wget http://192.168.50.4:480/root/helloworld/raw/master/cu-cli/create-server -O /tmp/create-server"
    sh "cat /tmp/create-server"
    sh "pwd"
    java '-jar /tmp/cloudunit-cli.jar --cmdfile /tmp/create-server'
}

def mvn(args) {
    sh "${tool 'M3'}/bin/mvn ${args}"
}

def java(args) {
    sh "${tool 'JAVA8'}/bin/java ${args}"
}
```
We introduce many concepts here as :
* the use of cloudunit-cli a new kid frol CloudUnitStack to manage the API through a CLI written in Spring shell
* the definition of a list of commands for CloudUnit into the **create-server**

The variables will defined into Jenkins configuration in the next chapter
* M3 for Maven installation
* JAVA8 for Java 8 installation

## Jenkins 2

Follow these steps during the first time to run and configure GitLab

Into the vagrantbox 
```
cd cloudunit/cu-platform
./jenkins2.sh
````
Docker will pull the image 
```
Unable to find image 'jenkinsci/jenkins:2.0-rc-1' locally
2.0-rc-1: Pulling from jenkinsci/jenkins
d8bd0657b25f: Pulling fs layer
a582cd499e0f: Pulling fs layer
3c3e582d88fa: Pulling fs layer
5901462573ab: Pull complete
6d048f1c223b: Pull complete
fd0e93cdc2d1: Pull complete
2f6ac4bc61e5: Pull complete
2fac80947c78: Pull complete
7f79e88ab057: Pull complete
Digest: sha256:beb15e5a612f0ef85b75fe98727aeb62d84ce023c1ebbcbdb0b1ff186c6eec8e
Status: Downloaded newer image for jenkinsci/jenkins:2.0-rc-1
b09e613e8f519b04ddf386d88232cc889b4749944c125804ff43292496146fbb
```

You can play this to verify if everything is good.

```
docker ps | grep jenkins2
jenkinsci/jenkins:2.0-rc-1      0.0.0.0:8081->8080/tcp, 0.0.0.0:50000->50000/tcp    jenkins2
```

So you have a container **jenkins2** exposing its ports at 
* HTTP open at 8081

