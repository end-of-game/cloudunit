# How to run from shell commands

First step : start the docker orchestrator

```
cd ~/cloudunit/cu-manager/cu-docker-orchestrator
mvn spring-boot:run -Drun.profiles=dev
```

Then start the domain manager

```
cd ~/cloudunit/cu-manager/cu-manager-domain
mvn spring-boot:run -Drun.profiles=dev
```

# How to run from IDE

## Eclipse

todo

## IntelliJ

todo

# Run the tests

todo