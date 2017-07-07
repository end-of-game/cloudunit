# CoreOs Clair
The Clair project is an open source engine that powers Quay Security Scanner to detect vulnerabilities in all images within Quay Enterprise, and notify developers as those issues are discovered.

# What is CoreOs Clair
Clair is an open source project for the static analysis of vulnerabilities in application containers (currently including appc and docker).
1. In regular intervals, Clair ingests vulnerability metadata from a configured set of sources and stores it in the database.
2. Clients use the Clair API to index their container images; this parses a list of installed source packages and stores them in the database.
3. Clients use the Clair API to query the database; correlating data is done in real time, rather than a cached result that needs re-scanning.
4. When updates to vulnerability metadata occur, a webhook containg the affected images can be configured to page or block deployments.
Our goal is to enable a more transparent view of the security of container-based infrastructure. Thus, the project was named Clair after the French term which translates to clear, bright, transparent.

`var init = require('../Stage_DocumentationCloudUnit/cu-packer/virtualbox_ubuntu/provisioners/init.sh');`

# Requirement CoreOs Clair in CloudUnit
1. Make sure that you have installed well CloudUnit
2. CoreOS Clair required:
    - Docker
    - Docker-compose 

# Installing CoreOS Clair into vagrant/Cloudunit
Install Clairctl:

 Docker-compose 
```
curl -L https://raw.githubusercontent.com/coreos/clair/master/docker-compose.yml -o docker-compose.yml
mkdir clair_config
curl -L https://raw.githubusercontent.com/coreos/clair/master/config.example.yaml -o clair_config/config.yaml
$EDITOR clair_config/config.yaml
docker-compose -f docker-compose.yml up -d
```

run clair: `docker-compose start clair`

# Installing Clairctl into vagrant/CloudUnit

Run this command: "make sure that you are root user before start or have privileged to install clairctl".

```
sudo curl -L https://raw.githubusercontent.com/jgsqware/clairctl/master/install.sh | sh
```

## Analyse, report to html Cloudunit images automatically

launch the script bash to get cloudunit containers vulnerability.

    analyze_CU.sh

## Requirement

    - docker daemon 1.17 minimum (tested)
    - docker API client and server should be the same version
    - make sure that your docker register is running

## Update docker engine 

```
docker-machine upgrade default
```

## Using Clairctl

After build, pull, commit an image, you should analyze this. In local environement the analyze is not automatic and require to use clairctl.

```
clairctl push -l myImageName
clairctl analyze -l myImageName
```

# Configuration clairctl file
After build, pull, commit an image, you should analyze this. In local environement the analyze is not automatic and require to use clairctl.

# Analyze report (HTML or JSON)
To report analysis, you should launch this command. You have two formats of reporting an analyse JSON or Html. 
Reports containers contains a list of sercurity failure (name, type, description link) for each layers.

```
clairctl report -l myImageName
```

