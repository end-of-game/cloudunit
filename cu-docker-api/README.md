# CloudUnit Docker Client

## Context

This project provides some Java methods to drive Docker API.
It is based on Docker API 1.18 (Docker server 1.6.2). The main Docker commands to manage containers,
images and run exec commands are available. It will be used into the next Cloudunit version. But you can easily use it
for your own projects using Docker

## Requirements

- Docker engine 1.6.2
- Maven 3.X +

## How it works

- Clone the project and install it on your local .m2 repository with Maven :

```bash
git clone git@github.com:Treeptik/cloudunit-docker-api.git
cd cloudunit-docker-api
mvn clean install -DskipTests
```

- Then, import the dependency in your project's pom.xml :

```xml
<dependency>
    <groupId>fr.treeptik</groupId>
    <artifactId>cloudunit-docker-client</artifactId>
    <version>1.0</version>
</dependency>
```

- All commands are provided by DockerClient class. You will find some examples in test directories

## What's next?

- Supports all available commands
- Upgrade to Docker Engine 1.8

