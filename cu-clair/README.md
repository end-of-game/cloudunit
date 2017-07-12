# CoreOs Clair for CloudUnit images

Clair is an open source project for the static analysis of vulnerabilities in application containers (currently including appc and docker).
1. In regular intervals, Clair ingests vulnerability metadata from a configured set of sources and stores it in the database.
2. Clients use the Clair API to index their container images; this parses a list of installed source packages and stores them in the database.
3. Clients use the Clair API to query the database; correlating data is done in real time, rather than a cached result that needs re-scanning.
4. When updates to vulnerability metadata occur, a webhook containg the affected images can be configured to page or block deployments.
Our goal is to enable a more transparent view of the security of container-based infrastructure. Thus, the project was named Clair after the French term which translates to clear, bright, transparent.

# Requirements

    - docker daemon 1.17 minimum (tested)
    - docker-compose 
    - docker API client and server should be the same version
    - make sure that your docker register is running 

If it's necessary, update your docker machine with `docker-machine upgrade`.

# Automatic installer Clair and clairctl

Install automatically clair and clairctl using `install.sh`.

```
    go to your ~/cloudunit/cu-clair/
    sudo ./install.sh
```

If Clair is not running: `docker-compose start Clair`

# Automatic analyser and HTML report

Analyse automatically CloudUnit containers vulnerabilities using `analyze_CU.sh`.

    ./analyze_CU.sh

Reports dir: `~/cloudunit/cu-clair/report/html`

## CLI to use clairctl manually

After build, pull image in a registry, you could analyze and report to html or json file.

```
    clairctl push -l myImageName
    clairctl pull -l myImageName
    clairctl analyze -l myImageName
```

```
Flags:
      --config string      config file (default is $HOME/clairctl.yml)
      --log-level string   log level [Panic,Fatal,Error,Warn,Info,Debug]
      --no-clean           Disable the temporary folder cleaning
```

## Reports

You have two formats JSON or Html(default). Reports containers contains a list of sercurity failure (name, type, description link) for each layers.

```
    clairctl report -l myImageName 
```

 
