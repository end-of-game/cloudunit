
# CloudUnit developement environment

This guide explains who to set up an environment to contribute to CloudUnit development.

## Requirements

- MacOS X 10.11 or 10.12
- Docker for Mac stable release (not egde)
- Git 2.7.x
- Java 8 JDK
- Node 5.12.x
- Java IDE: [IntelliJ IDEA 2017.1](https://www.jetbrains.com/idea/), or [Spring Tool Suite 3.8.4](https://spring.io/tools), [Eclipse Neon.3 (4.6.3)](http://www.eclipse.org/downloads/)
- Node IDE: SublimeText, [Atom 1.16.x](https://atom.io/)
- VirtualBox 5.0.x (http://www.virtualbox.org or `sudo apt install virtualbox`)
- Maven 3.3.9

## Architecture

- Docker for Mac to provide services such as MongoDB and RabbitMQ.
- The backend is a collection of Spring Boot microservices exposing REST APIs
- The frontend is an AngularJS 1.4 consuming the backend API
- A CLI also consumes the backend API

## Projects

- `cu-manager`: CloudUnit Manager backend, Maven project
- `cu-manager-ui`: CloudUnit Manager frontend, Node project
- `cu-cli`: CLI, Node project
- `cu-compose`: Shell scripts and Docker Compose files for running CloudUnit
- `cu-services`: Docker images

## Setup

### Clone source code

```
[MacOSX]    git clone https://github.com/Treeptik/cloudunit.git
[MacOSX]    cd cloudunit
[MacOSX]    git checkout dev-3.x
```

### Install Node Project dependencies

```
[MacOSX]    npm install -g grunt grunt-cli bower 
[MacOSX]    pushd cu-manager-ui && npm install && bower install && popd
[MacOSX]    pushd cu-cli && npm install && popd
```

### Build images

```
[MacOSX]    pushd cloudunit/cu-services && make && popd
```

### Start supporting services

```
[MacOSX]    pushd cloudunit/cu-compose && ./reset-dev.sh -y && popd
```

### Import Maven projects into Java IDE workspace

From your favorite Java IDE, import the Maven project located at the root of the Git repository.

### Import Node projects

From your favorite Node IDE add `cu-cli` and `cu-manager-ui` folders to your workspace.

### Run CloudUnit Manager backend

#### From IntelliJ IDEA

Create Spring Boot run configurations for `cu-docker-orchestrator` and `cu-manager-domain` that activate the `local` profile.

#### From Spring Tool Suite

From the **Boot Dashboard** view, start `cu-docker-orchestrator` and `cu-manager-domain` with the `local` profile active.

#### From Eclipse

Create run configurations for the main classes in `cu-docker-orchestrator` and `cu-manager-domain`.
Before running, add the following VM argument in order to activate the `local` profile: `-Dspring.profiles.active=local`.

#### From command line

In two different shells (maybe using tmux, screen, or byobu) run
```
[MacOSX]    cd cu-manager
[MacOSX]    mvn spring-boot:run -pl cu-docker-orchestrator -Plocal
```
and
```
[MacOSX]    cd cu-manager
[MacOSX]    mvn spring-boot:run -pl cu-manager-domain -Plocal
```

### Run CloudUnit Manager frontend

```
[MacOSX]    cd cu-manager-ui
[MacOSX]    mvn spring-boot:run -pl cu-manager-ui -Plocal
```
This will open http://0.0.0.:9000/ in your default web browser.

# FAQ

## How to reset Development Environment

```
[MacOSX]    pushd cloudunit/cu-compose && ./reset-dev.sh -y && popd
```
    
## How to rebuild images

```
[MacOSX]    pushd cloudunit/cu-services && make && popd
[MacOSX]    pushd cloudunit/cu-compose && ./reset-dev.sh -y && popd
```

