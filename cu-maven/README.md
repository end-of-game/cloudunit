# CloudUnit Maven Plugin

This is the official Cloudunit Maven Plugin to automate your Java web application deployments into Cloudunit Paas via Maven

## Requirements

- JDK 7 +
- Maven 3.X +
- Java web application built with Maven
- Available Cloudunit platform

## How it works

- Clone the project and install the plugin into your m2 local repository :

```bash
git clone git@github.com:Treeptik/cloudunit-maven-plugin.git
cd cloudunit-maven-plugin/
mvn clean install
```

- Add the plugin into the POM file of the project "mysuperproject" you want to deploy :

```xml
<build>
		<plugins>
			<plugin>
				<groupId>fr.treeptik</groupId>
				<artifactId>cloudunit-maven-plugin</artifactId>
				<version>1.0</version>
				<configuration>
				
				<!-- Non required : by default, the plugin uses a Cloudunit Manager 
				installed on your local host on 8080 port (http://127.0.0.1:8080)-->
				
					<managerLocation>127.0.0.1</managerLocation>
					<managerPort>8080</managerPort>
					
				<!-- Required : your Cloudunit credentials and the app you want to deploy on. 
				You must create the app before, add modules and parameters before using it-->
						
					<username>mylogin</username>
					<password>mysecretpassword</password>
					
					<applicationName>mysuperproject</applicationName>
					
				</configuration>
			</plugin>
		</plugins>
</build>
```
- Package and deploy your application

```bash
cd mysuperproject
mvn clean package cloudunit:deploy
```
- Enjoy!

## Advanced features

- Before deploying, create your application and initialize modules automatically.
Be careful, you cannot change the application configuration through the maven plugin
after the first deployment.

```xml
<configuration>
				<!-- #PREVIOUS SETTINGS -->
				
				<!-- Non required : the default value is false -->
					<createIfNotExists>true</createIfNotExists>
										
				<!-- Required if you set 'true' to the property 'createIfNotExists' -->				
					<server>tomcat-8</server>
					
				<!-- Non required : you can add several modules as parameters to this array -->				
					<modules>
						<param>mysql-5-5</param>
						<param>postgresql-9-3</param>
					</modules>					
</configuration>
```

- Snapshot your previous application state before sending a new deployment

```xml
<configuration>
				<!-- #PREVIOUS SETTINGS -->
				
				<!-- Non required. The default value is false -->
				<snapshotOnDeploy>true</snapshotOnDeploy>
				
</configuration>
```


## Roadmap

- Make it available on Maven public repositories
- Automate jvm configurations
- Add a secured token to hide user credentials in plugin configurations
- And more...

## Licensing

All the source code is licensed under GNU AFFERO GENERAL PUBLIC LICENSE. License is available [here](/LICENSE.txt)

Copyright 2015 Treeptik


