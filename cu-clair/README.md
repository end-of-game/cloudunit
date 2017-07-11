# CoreOs Clair
The Clair project is an open source engine that powers Quay Security Scanner to detect vulnerabilities in all images within Quay Enterprise, and notify developers as those issues are discovered.

# What is CoreOs Clair
Clair is an open source project for the static analysis of vulnerabilities in application containers (currently including appc and docker).
1. In regular intervals, Clair ingests vulnerability metadata from a configured set of sources and stores it in the database.
2. Clients use the Clair API to index their container images; this parses a list of installed source packages and stores them in the database.
3. Clients use the Clair API to query the database; correlating data is done in real time, rather than a cached result that needs re-scanning.
4. When updates to vulnerability metadata occur, a webhook containg the affected images can be configured to page or block deployments.
Our goal is to enable a more transparent view of the security of container-based infrastructure. Thus, the project was named Clair after the French term which translates to clear, bright, transparent.

# CoreOs clair in CloudUnit

CoreOS clair required:

    - Docker
    - Docker-compose 

# Installing CoreOS Clair into Cloudunit

Install clair by docker-compose.yml file.

```
    docker-compose up -d
```

If clair is not running: `docker-compose start clair`

# Installing Clairctl into vagrant/CloudUnit

Install clairctl by curl.

```
sudo su 
curl -L https://raw.githubusercontent.com/jgsqware/clairctl/master/install.sh | sh
```


# Analyse, report to html Cloudunit images automatically

Analyse cloudunit containers vulnerability by analyze_CU.sh file.

    ./analyze_CU.sh

# How to use clairctl

## Requirement

    - docker daemon 1.17 minimum (tested)
    - docker API client and server should be the same version
    - make sure that your docker register is running

## Update docker engine 

```
docker-machine upgrade default
```

## CLI for Clairctl

After build, pull image in a registry, you could analyze and report to html or json file.

```
clairctl push -l myImageName
clairctl pull -l myImageName
clairctl analyze -l myImageName
```

## Reports
You have two formats JSON or Html(default). Reports containers contains a list of sercurity failure (name, type, description link) for each layers.

```
clairctl report -l myImageName 
```

