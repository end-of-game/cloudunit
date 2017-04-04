# Start Vagrant box and supporting containers

Go to `cu-vagrant` directory. Then start the Vagrant box and connect to it by running one of the scripts `smallbox.sh`, `mediumbox.sh` or `largebox.sh`.

Once inside, run the following commands:

```
cd cloudunit/cu-compose
./reset-dev.sh -y
```

# Build Docker images

Go to `cu-services` directory.

Run `make` to build all Docker images. Alternatively run `make <image name>` or `make <image name>_<version>` to build a specific image or even a specific version of an image.

# How to run from shell commands


## Start the Docker Orchestrator microservice

Go to `cu-manager` directory. Then run:
```
mvn spring-boot:run -pl cu-docker-orchestrator
```

## Start the Manager Domain microservice

Go to `cu-manager` directory. Then run:

```
mvn spring-boot:run -pl cu-manager-domain
```

# How to run from IDE

## Eclipse

todo

## IntelliJ

todo

# Run the tests

From the `cu-manager` directory, run `mvn verify` to run both unit and integration tests.

**For the moment, the Manager Domain integration tests cannot succeed unless you manually start the Docker Orchestrator!**
