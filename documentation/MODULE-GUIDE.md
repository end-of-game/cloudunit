# How to add a module to CloudUnit

This guide will show you how to register new modules in CloudUnit for use in your application.

Before following the instructions, first set up your CloudUnit development environment by refering to the appropriate
guide:

- for [Linux](DEV-GUIDE-LINUX.md)
- for [MacOS](DEV-GUIDE-MACOS.md)

When in doubt about how to complete any of the steps in this guide, refer to the module for
[PostgreSQL 9.3](cu-services/images/modules/postgresql-9-3).

## Create a Docker image

All module images are stored in a specific directory in the CloudUnit sources. Navigate to that directory.

```
cd cu-services/images/modules
```

Create a directory for your module. In our example we'll name it `mymodule`.

```
mkdir mymodule
cd mymodule
```

### Write the `Dockerfile`

During this step, you will write a `Dockerfile` in the `mymodule` directory you just created.

If your module is based on an official image from Docker Hub, you should use the official `Dockerfile` as a starting point.
Otherwise, bring your own `Dockerfile`.

Since CloudUnit requires a few extra tools to be installed inside each image, the module's `Dockerfile` must be based on one
of the base images provided:

- `cloudunit/base-12.04`, `cloudunit/base-14.04`, `cloudunit/base-16.04` built on top of the official Ubuntu Docker images;
- `cloudunit/base-jessie` built on top of the official Debian Jessie Docker image.

Replace the `FROM` command in your `Dockerfile` with the appropriate CloudUnit base image. For instance, if the `Dockerfile` you
are adapting starts with the instruction `FROM ubuntu:14.04` or `FROM ubuntu:trusty` then replace it with `FROM cloudunit/base-14.04`.

### Add environment variables

The following environment variables, required by CloudUnit, must be added to the `Dockerfile`:

- `CU_MODULE_PORT`: the port that is exposed by the module, for example 5432 for PostgreSQL.
- `CU_DEFAULT_LOG_FILE`: for future use in exported log files. Leave blank.
- `CU_LOGS`: ditto.

### Add supporting files and scripts

The `Dockerfile` alone is not enough to build the image and have it loaded by CloudUnit.

Firstly, the `Dockerfile` may reference files or directories that must be added to the image. Place these supporting
files next to the `Dockerfile`. For example, a lot of images require a Docker entrypoint script, usually named
`docker-entrypoint.sh`.

Secondly, CloudUnit requires 4 scripts to be present inside the image:

- `env.sh` contains a simple call to the `env` command.
- `check-running.sh` allows CloudUnit to know when the service provided by the image is up, running and ready to accept
connections. Most notably, when a module is started, this script is run over and over again until it all it does is
echo `0`.
- `backup-data.sh` and `restore-data.sh` allow CloudUnit to take snapshots of the application as a whole. These
snapshots can then be used to rollback the application in case of failure, or to clone new instances more quickly.

Finally, specific scripts may be added to the module. They will be accessible under the Scripting screen of the module
where the user will be able to execute them on demand.

All of these scripts should be placed in a subdirectory of module's directory named `scripts`. In order to include them
in the image, the following instruction must be added to the `Dockerfile`.

```
ADD scripts /opt/cloudunit/scripts
RUN chmod +x /opt/cloudunit/scripts/* 
```

### Build the image

While inside the CloudUnit Vagrant box, build the image so it will be present in the local Docker instance.

```
cd ~/cloudunit/cu-services/
docker build --rm --no-cache -t cloudunit/mymodule images/modules/mymodule
```

You may want to encapsulate this Docker command in a script in the `~/cloudunit/cu-services` directory, especially if
you plan to provide several versions of the module. For example, PostgreSQL version 9.3, 9.4 and 9.5 are currently
available as modules, with a single script named `postgre.sh` building all three images.

## Register the module with CloudUnit

The final step that will allow CloudUnit to see your module is to add the necessary information to the database. This
database is initialized by a SQL script found at `cu-manager/src/main/resources/db.init.sql`.

There are two tables that must contain information about the module:

- `Image` contains general information about the module's image:
  * `id` a unique identifier that is the table's primary key. It is assigned manually. For your module, pick one that
    has not been used.
  * `name` a unique name for the module.
  * `path` the tag given to the module's image when built by Docker.
  * `displayName` the name to be displayed by the CloudUnit manager.
  * `prefixEnv` a prefix that is used to group multiple versions of the same module. Bear in mind that applications may
    not contain multiple instances of a modules of the same prefix.
  * `imageType` always contains the value `module` for modules.
  * `managername` is obsolete.
  * `exposedPort` contains the number of the port that the module exposes.
- `Image_moduleEnvironmentVariables` contains one line for each of the environment variables that are required when the
  image is run
  * `moduleEnvironmentVariables` contains the name of the variable
  * `moduleEnvironmentVariables_KEY` contains the role of the variable, which may be one of `USER` if the variable
    must contain the name of a user to be created within the service that the module exposes, `PASSWORD` for the
    password that must be associated with that user, or `NAME` if the variable must contain the name of the
    application.
  * `Image_id` is a foreign key referencing the `Image` table.

When a module is added, CloudUnit will generate a random username and password. Whenever the module is run, these
credentials will be provided to the module's container using the environment variable names configured in the
`Image_moduleEnvironmentVariables` table. They will also be injected into the application's server as environment
variables. The following naming convention is used: `CU_DATABASE_<module prefix>_<variable role>`. For example, if the
PostgreSQL module is present in an application, the database username is injected into the server as an environment
variable named `CU_DATABASE_POSTGRESQL_USER`. In addition, the a variable named `CU_DATABASE_DNS_<module prefix>`
will be set to the hostname of the module. These variables can then be used to configure the application that is
deployed.

## Test the module

Now that everything is set up for using your new module, test it by creating a new application and adding the module to
it.
