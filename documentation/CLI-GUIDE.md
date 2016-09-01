# CloudUnit-CLI guide

This guide is for a user who want to use CloudUnit with command line interface.

## Requirements

For use this application, you need to have installed before :

 - Available CloudUnit Manager 1.0
 - Java Runtime Environment 1.7 or more

## Download and launch the application

You can download the archive of application [here](https://github.com/Treeptik/CloudUnit/releases/download/1.0/cloudunitcli.zip).
Or use :
```bash
git clone git@github.com:Treeptik/CloudUnit-CLI.git
```

After download, you can launch the application by using these commands :
```bash
cd CloudUnit-CLI
mvn clean compile exec:java
```

You can run this application with Docker too :
```bash
docker run --rm -it cloudunit/cli
```

## Help

When the application is running, you can access to help with the command *help*. This command will display all commands available in CloudUnit-CLI.
```bash
cloudunit-cli> help
```
If you wanna have an help on a specific command, you can use *help* with command in argument :
```bash
cloudunit-DEV-myapp>  help connect
Keyword:                   connect
Description:               Connect to CloudUnit Host
 Keyword:                  login
   Help:                   Your login
   Mandatory:              true
   Default if specified:   '__NULL__'
   Default if unspecified: '__NULL__'

 Keyword:                  password
   Help:                   User password
   Mandatory:              false
   Default if specified:   '__NULL__'
   Default if unspecified: ''

 Keyword:                  host
   Help:                   Host for Cloudunit Platform
   Mandatory:              false
   Default if specified:   '__NULL__'
   Default if unspecified: ''

* connect - Connect to CloudUnit Host
```

If you give a pattern in argument, the *help* command provides you commands with this pattern : 
```bash
cloudunit-DEV-myapp>  help rm
* rm-alias - Remove an existing alias
* rm-app - Remove an application
* rm-module - Remove a module from the current application
* rm-snapshot - Remove the snapshot for the current application
```

## Connection

You can connect to the CloudUnit Manager with the command :
```bash
cloudunit-cli> connect --login johndoe 
Enter your password : 
*******
Trying to connect to default CloudUnit host...
Connection established
```
Default host is localhost(/127.0.0.1) but you can specify if you want an external host with the command :
```bash
cloudunit-cli> connect --login johndoe --host https://admin.cloudunit.dev
Enter your password : 
*******
Trying to connect to default CloudUnit host...
Connection established
```
Now you are in the DEV context.

## Application features

### Create

You can create an application with the command :
```bash
cloudunit-DEV> create-app --name name --type tomcat-7
Your application myapp is currently being installed
```
After this command, you are in the application context.

### Remove

You can delete an existing application : 
```bash
cloudunit-DEV-myapp>  rm-app --name test
test
Confirm the suppression of your application: test - (yes/y) or (no/n)
yes
Your application test is currently being removed
```

### Change context

When you have two or more applications created on CloudUnit, you can take control of an application with the command :

```bash
cloudunit-DEV-myapp>  use myapp2
Current application : myapp2
```

### Clone

**For this feature, you must have already created a snapshot.**
You can clone an existing application :
```bash
cloudunit-DEV-myapp>  clone --tag tag --applicationName myapp
Your application myapp2 was successfully created.
```

### Display

You can display all informations about the current application :
```bash
cloudunit-DEV-myapp>  informations

 GENERAL 

+----------------+--------+----------------+-----------+------+------------+
|APPLICATION NAME|AUTHOR  |STARTING DATE   |SERVER TYPE|STATUS|JAVA VERSION|
+----------------+--------+----------------+-----------+------+------------+
|myapp           |Doe John|2016-06-03 13:23|TOMCAT-7   |START |jdk1.7.0_55 |
+----------------+--------+----------------+-----------+------+------------+

 GIT ADDRESS 

+----+--------------+
|TYPE|REMOTE ADDRESS|
+----+--------------+
|GIT |(NULL)        |
+----+--------------+

 SERVER INFORMATION 

+--------+-------------+--------+------+--------+------+---------------------------------------------------------------+
|TYPE    |ADDRESS      |SSH PORT|STATUS|JVM OPTS|MEMORY|MANAGER LOCATION                                               |
+--------+-------------+--------+------+--------+------+---------------------------------------------------------------+
|TOMCAT-7|cloudunit.dev|32774   |FAIL  |NONE    |1024  |http://manager-myapp-johndoe-admin.cloudunit.dev/manager/html? |
+--------+-------------+--------+------+--------+------+---------------------------------------------------------------+

 MODULES INFORMATION 

No modules found!
Terminated
```

### Listing applications

You can list all applications :
```bash
cloudunit-DEV>  list-apps
+----------------+--------+----------------+-----------+------+
|APPLICATION NAME|AUTHOR  |STARTING DATE   |SERVER TYPE|STATUS|
+----------------+--------+----------------+-----------+------+
|test            |Doe John|2016-06-03 11:00|jboss-8    |START |
+----------------+--------+----------------+-----------+------+
|myapp           |Doe John|2016-06-03 13:23|tomcat-7   |START |
+----------------+--------+----------------+-----------+------+
|myapp2          |Doe John|2016-06-03 13:48|tomcat-6   |START |
+----------------+--------+----------------+-----------+------+
|myapp3          |Doe John|2016-06-03 14:07|tomcat-7   |START |
+----------------+--------+----------------+-----------+------+
|myapp4          |Doe John|2016-06-03 14:32|tomcat-7   |STOP  |
+----------------+--------+----------------+-----------+------+
|myapp5          |Doe John|2016-06-03 15:02|tomcat-6   |START |
+----------------+--------+----------------+-----------+------+
6 found !
```

### Start and Stop

#### Start

You can start the application with *start* command. This command starts all services of this application too :
```bash
cloudunit-DEV-myapp>  start
Your application myapp is currently being started
```

#### Stop

After your application was started, you can stop it and its services with *stop* command :
```bash
cloudunit-DEV-myapp>  stop
Your application myapp is currently being stopped
```

## Features on an application

### Listing containers

You can list all containers of an application :
```bash
cloudunit-DEV-test>  list-containers
+-------------------------+
|CONTAINER NAME           |
+-------------------------+
|dev-johndoe-test-tomcat-6|
+-------------------------+
1 containers found!
```

### Environment variables

#### Add

You can create an environment variable to an application with this command :

```bash
cloudunit-DEV-test>  create-var-env --key key --value value
test
An environment variable has been successfully added to test
```

Accents and specials characters are forbbiden in keys.

#### Remove

You can remove an environment variable if you use the command *rm-var-env* :
```bash
cloudunit-DEV-test>  rm-var-env --key key
This environment variable has successful been deleted
```

#### Listing

You can list all environment variables of an application :
```bash
cloudunit-DEV-test>  list-var-env
+-----------------------------+------+
|CURRENT ENVIRONMENT VARIABLES|VALUES|
+-----------------------------+------+
|key                          |value |
+-----------------------------+------+
1 variables found!
```

#### Update

You can update a environment variable by modifying key, value or both.
```bash
cloudunit-DEV-test>  update-var-env --old-key key --new-key keyUpdated --value valueUpdated
This environment variable has successful been updated
```

### Aliases

#### Add

You can add an alias to an application with this command :

```bash
cloudunit> add-alias --alias treeptik.fr
An alias has been successfully added to myapp
```

Aliases must to respect some pattern (treeptik.fr, treeptik-test.fr, treeptik123.corp.eu)

#### Remove

You can remove an alias if you use the command *rm-alias* :
```bash
rm-alias --alias treeptik.fr
This alias has successful been deleted
```

#### Listing

You can list all aliases of an application :
```bash
cloudunit-DEV-myapp>  list-aliases
+---------------+
|CURRENT ALIASES|
+---------------+
|treeptik.fr    |
+---------------+
1 aliases found!
```

### Ports

#### Add

You can open a port :
```bash
cloudunit-DEV-myapp>  open-port --name port --port 8080 --nature http
port
```

#### Remove

You can remove a opened port :
```bash
cloudunit-DEV-myapp>  remove-port --name port --port 8080
port
```

### Java options

#### Add

You can add Java options (except memory options) to an application with this command :

```bash
cloudunit-DEV-myapp> add-jvm-option "option"
Add java options to myapp application successfully
```

#### Change

You can change option of Java in your application :

- Change the version of Java which your application use :
```bash
cloudunit-DEV-myapp> change-java-version --javaVersion jdk1.7.0_55
Your java version has been successfully changed
```
You have access to two versions of Java :  jdk1.8.0_25 and jdk1.7.0_55.

- Change the memory used by Java :
```bash
cloudunit-DEV-myapp> change-jvm-memory --size 512
Change memory on myapp successful
```
You have four options for memory : 512, 1024, 2048, 3072.
By default, an application has memory of 512.

### Modules

#### Add
You can add a module on your application with this command :
```bash
cloudunit-DEV-myapp> add-module --name mysql-5-5
myapp
Your module mysql-5-5 is currently being added to your application myapp
```
You have four modules available : MySQL 5.5 (mysql-5-5), POSTGRES 9.3 (postgres-9-3), REDIS 3.0 (redis-3-0), MONGO 2.6 (mongo-2-6).

#### Remove

You can remove a existing module in an application :
```bash
cloudunit-DEV-myapp>  rm-module --name mysql-5-5
myapp
Your module mysql-5-5 is currently being removed from your application myapp
```

#### Listing

You can display informations about all modules of an application with the command *display-modules* :
```bash
cloudunit-DEV-myapp>  display-modules
MODULES INFORMATION

+-----------+---------------------------------------------------------------+
|MODULE NAME|mysql-5-5-1                                                    |
+-----------+---------------------------------------------------------------+
|TYPE       |mysql-5-5                                                      |
+-----------+---------------------------------------------------------------+
|DOMAIN NAME|johndoe-myapp-mysql-5-5-1.mysql-5-5.cloud.unit                 |
+-----------+---------------------------------------------------------------+
|PORT       |3306                                                           |
+-----------+---------------------------------------------------------------+
|USERNAME   |adminom8cdo5u                                                  |
+-----------+---------------------------------------------------------------+
|PASSWORD   |p3v19ir0                                                       |
+-----------+---------------------------------------------------------------+
|DATABASE   |myapp                                                          |
+-----------+---------------------------------------------------------------+
|MANAGER    |http://phpmyadmin1-myapp-johndoe-admin.cloudunit.dev/phpmyadmin|
+-----------+---------------------------------------------------------------+
```

### Snapshots

#### Create

You can create a snapshot of an application using :
```bash
cloudunit-DEV-myapp>  create-snapshot --tag tag1 --applicationName myapp
myapp
A new snapshot called tag1 was successfully created.
```

#### Remove

You can delete a snapshot of an application :
```bash
cloudunit-DEV>  rm-snapshot --tag tag1
The snapshot tag1 was successfully deleted.
```

If an application use the template which you want to remove :
```bash
cloudunit-DEV-myapp>  rm-snapshot --tag tag1
At least one application uses this template. You must delete it before.
```

#### Listing

You can list all snapshots of an application :

```bash
cloudunit-DEV-myapp>  list-snapshot
No snapshots found!
0 snapshots found
```

### Deploy an archive on your application

You can deploy an archive on your application.
**This archive have to be in ear or war file type.**
```bash
cloudunit-DEV-myapp>  deploy --path ~/cloudunit-webapp-examples/pizzaiolo-mysql/target/pizzashop-1.0.0.war --openBrowser true
War deployed - Access on http://myapp-johndoe-admin.cloudunit.dev
```

## Globales features

### Volume management

#### Create a volume

You can create a volume with this command :
```bash
cloudunit-DEV>  create-volume --name volumeTest
The volume volumeTest was been successfully created
```

#### Remove a volume

You can remove a volume :
```bash
cloudunit-DEV>  rm-volume --name volumeTest
This volume has successful been deleted
```

#### Mount a volume on a application

When you have created your volume, you can mount it on an existant application (**You must use an absolute path for this command**) :
```bash
cloudunit-DEV-test>  mount-volume --volume-name volumeTest --path /cloudunit/ --container-name dev-johndoe-test-tomcat-6 --application-name test
This volume has successful been mounted
```

#### Unmount a volume on a application

Once your volume mount on your application, you can unmount it with the command :
```bash
cloudunit-DEV-test>  unmount-volume --container-name dev-johndoe-test-tomcat-6 --volume-name volumeTest
This volume has successful been unmounted
```

### Clear the console

You can clear the console by two ways :
```bash
cloudunit-DEV-myapp> clear
```
```bash
cloudunit-DEV-myapp> cls
```

### Get the current date and time

You can have the current date and time :
```bash
cloudunit-DEV-myapp>  date
vendredi 3 juin 2016 14 h 34 CEST
```

### Get exit code of the last CU command executed

For get this informations, you need to use the command *echo* :
```bash
cloudunit-DEV-myapp>  echo
0
```

### Display all shell's properties

You can see all shell's variables with the command *shell properties* : 
```bash
cloudunit-DEV-myapp>  system properties
awt.toolkit = sun.awt.X11.XToolkit
classworlds.conf = /usr/share/maven/bin/m2.conf
...
user.language = fr
user.name = username
user.timezone = Europe/Paris
```

### Version

You can the version of CloudUnit CLI with the command *version* :
```bash
cloudunit-DEV-myapp>  version
1.0
```

### Access to operating system (OS) shell

If you use ! before a command, the shell executes the command like an OS command : 
```bash
cloudunit-DEV-myapp>  ! echo "Hello World !"
command is:echo "Hello World !"
"Hello World !"
```

### Automating

If you wanna execute some commands in a file, you can use the command *script* :
```bash
cloudunit-cli> script --file ../../Connect.sh
connect --login johndoe --password abc2015
Trying to connect to default CloudUnit host...
Connection established
Script required 0.731 seconds to execute
```
**You can only execute commands available on CloudUnit-CLI, not OS commands.**

### Comments

The *//* and *;* characters, if these characters start lines, allow to print comments : 
```bash
cloudunit-DEV-myapp>  // Remove an application
cloudunit-DEV-myapp>  ; Remove an application
cloudunit-DEV-myapp>  rm-app --name myapp
myapp
Confirm the suppression of your application: myapp - (yes/y) or (no/n)
yes
Your application myapp is currently being removed
```

### Exit the shell

For exit CloudUnit-CLI and return the standard shell, you need to use this command : 
```bash
cloudunit-DEV-myapp>  exit
```

You can use the command *quit* too : 
```bash
cloudunit-DEV-myapp>  quit
```

## Disconnection

You can disconnect with the command *disconnect* :
```bash
cloudunit-DEV-myapp>  disconnect
Disconnect
```

## Script exemple for create and deploy an application

This is an exemple of script which connect to an account, create an application, download a git repository, package it and deploy it on CloudUnit-CLI :

```bash
connect --login johndoe --password abc2015
create-app --name myapp --type tomcat-7
! git clone https://github.com/Treeptik/cloudunit-webapp-examples.git
! mvn clean package -f cloudunit-webapp-examples/spring-boot-mongodb/pom.xml
deploy --path cloudunit-webapp-examples/spring-boot-mongodb/
disconnect
```
Now, you'll just run the command :

```bash
cloudunit-cli> script --file /home/username/script.sh
```
