# CloudUnit Command Line interface 1.0

## What is it?

CloudUnit CLI is a command line client for [CloudUnit platform 1.0](https://github.com/Treeptik/CloudUnit) writing in Java (based on Spring shell 1.1.0). You can access all main available features of CloudUnit PaaS using it.

## Getting started

Refer to our [guide](https://github.com/Treeptik/cloudunit-cli/blob/dev/documentation/GETTING_STARTED.md) to start using CloudUnit CLI from a delivery on Linux/MAC or Windows.

## Requirements for Dev mode

If you want to take part of CloudUnit CLI development, you will need :

- JDK 1.7 +
- Maven 3.X

Clone the CloudUnit CLI 1.0 project and develop. Use the exec maven plugin to build and start it quicky as following :

```bash
git clone git@github.com:Treeptik/CloudUnit-CLI.git
cd CloudUnit-CLI
mvn clean compile exec:java

```

## Run it with Docker

```
docker run --rm -it cloudunit/cli
```


##License

All the source code is licensed under GNU AFFERO GENERAL PUBLIC LICENSE. License is available [here](https://github.com/Treeptik/CloudUnit/blob/master/LICENSE) but CloudUnit is licensed too under a standard commercial license. Please contact our sales team if you would like to discuss the specifics of our Enterprise license. If you are not sure whether the AGPL is right for you, you can always test our software under the AGPL and inspect the source code before you contact us about purchasing a commercial license.

##Legal term

CloudUnit is a registered trademark of Treeptik and can't be used to endorse or promote products derived from this project without prior written permission from Treeptik. Products or services derived from this software may not be called CloudUnit nor may Treeptik or similar confusing terms appear in their names without prior written permission.

##Contact

For any questions, contact us : contact@treeptik.fr

Copyright 2016 Treeptik
