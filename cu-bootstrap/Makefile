.PHONY: build run all

DOCKER_IMAGE_NAME=cloudunit/bootstrap

all: build run

clean:
    docker rm -vf cu

build:
	docker build \
		--tag $(DOCKER_IMAGE_NAME) \
		./

run:
    docker run \
        -v /var/run/docker.sock:/var/run/docker.sock \
        cloudunit/bootstrap
