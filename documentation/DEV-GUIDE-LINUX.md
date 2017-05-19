
# CloudUnit developement environment

You are reading the wright guide, if you want to setup an environment to contribute to CloudUnit development.

## Requirements

- Linux: Ubuntu 14.04, Ubuntu 16.04, or Debian Jessie
- Git 2.7.x
- Java 8 JDK
- Node 5.12.x (`sudo apt install nodejs`)
- Java IDE: [IntelliJ IDEA 2017.1](https://www.jetbrains.com/idea/), or [Spring Tool Suite 3.8.4](https://spring.io/tools), [Eclipse Neon.3 (4.6.3)](http://www.eclipse.org/downloads/)
- Node IDE: SublimeText, [Atom 1.16.x](https://atom.io/)
- VirtualBox 5.0.x (http://www.virtualbox.org or `sudo apt install virtualbox`)
- Vagrant 1.9.x (http://www.vagrantup.com)
- Maven 3.3.9 (`sudo apt install maven`)

## Architecture

- A VagrantBox houses Docker and supporting services, such as MongoDB and RabbitMQ.
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

### Install Vagrant plugins

```
[Linux Host]    sudo apt install ruby-dev
[Linux Host]    sudo vagrant plugin install vagrant-reload
[Linux Host]    sudo vagrant plugin install vagrant-vbguest
```

### Clone source code

```
[Linux Host]    git clone https://github.com/Treeptik/cloudunit.git
[Linux Host]    cd cloudunit
[Linux Host]    git checkout dev-3.x
```

### Install Node Project dependencies

```
[Linux Host]    sudo npm install -g grunt grunt-cli bower 
[Linux Host]    pushd cu-manager-ui && npm install && bower install && popd
[Linux Host]    pushd cu-cli && npm install && popd
```

### Start VagrantBox and connect to it via SSH

Warning! This step can take a while the first time.

If your machine has 16 GB RAM
```
[Linux Host]    pushd cu-vagrant && ./mediumbox.sh && popd
```

Or, if your machine has 8 GB RAM
```
[Linux Host]    pushd cu-vagrant && ./smallbox.sh && popd
```

### Build images

```
[VagrantBox]    pushd cloudunit/cu-services && make && popd
```

### Start supporting services

```
[VagrantBox]    pushd cloudunit/cu-compose && ./reset-dev.sh -y && popd
```

### Import Maven projects into Java IDE workspace

From your favorite Java IDE, import the Maven project located at the root of the Git repository.

### Import Node projects

From your favorite Node IDE add `cu-cli` and `cu-manager-ui` folders to your workspace.

### Run CloudUnit Manager backend

#### From IntelliJ IDEA

Create Spring Boot run configurations for `cu-docker-orchestrator` and `cu-manager-domain` that activate the `vagrant` profile.

#### From Spring Tool Suite

From the **Boot Dashboard** view, start `cu-docker-orchestrator` and `cu-manager-domain` with the `vagrant` profile active.

#### From Eclipse

Create run configurations for the main classes in `cu-docker-orchestrator` and `cu-manager-domain`.
Before running, add the following VM argument in order to activate the `vagrant` profile: `-Dspring.profiles.active=vagrant`.

#### From command line

In two different shells (maybe using tmux, screen, or byobu) run
```
[Linux Host]    cd cu-manager
[Linux Host]    mvn spring-boot:run -pl cu-docker-orchestrator -Pvagrant
```
and
```
[Linux Host]    cd cu-manager
[Linux Host]    mvn spring-boot:run -pl cu-manager-domain -Pvagrant
```

### Run CloudUnit Manager frontend

```
[Linux Host]    cd cu-manager-ui && grunt serve
```
This will open http://0.0.0.:9000/ in your default web browser.

# FAQ

## How to reset Development Environment

```
[VagrantBox]    pushd cloudunit/cu-compose && ./reset-dev.sh -y && popd
```
    
## How to rebuild images

```
[VagrantBox]    pushd cloudunit/cu-services && make && popd
[VagrantBox]    pushd cloudunit/cu-compose && ./reset-dev.sh -y && popd
```

## How to run e2e test (selenium & protractor)

First of all, you have to install Google Chrome.
Then, start the application ([see step 6](#step6)) in parallel.

```
[Linux Host]    pushd cu-manager-ui && grunt test && popd
```
