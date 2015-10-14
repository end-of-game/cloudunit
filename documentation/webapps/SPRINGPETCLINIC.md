# SPRING PETCLINIC

## Clone the opensource project from SpringSource
```
git clone https://github.com/SpringSource/spring-petclinic.git
```
## Database access

Edit the following files:

* Add into pom.xml
```
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.22</version>
</dependency>
```
* Comment the lines about HSQL
* Uncomment the lines into **src/main/resources/spring/data-access.properties* *
```
jpa.showSql=true
 
#-------------------------------------------------------------------------------
# MySQL Settings
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://${CU_DATABASE_DNS_1}:3306/${CU_DATABASE_NAME}
jdbc.username=${CU_DATABASE_USER_1}
jdbc.password=${CU_DATABASE_PASSWORD_1}
 
# Properties that control the population of schema and data for a new data source
jdbc.initLocation=classpath:db/mysql/initDB.sql
jdbc.dataLocation=classpath:db/mysql/populateDB.sql
 
# Property that determines which Hibernate dialect to use
# (only applied with "applicationContext-hibernate.xml")
hibernate.dialect=org.hibernate.dialect.MySQLDialect
 
# Property that determines which database to use with an AbstractJpaVendorAdapter
jpa.database=MYSQL
```
It is good to understand how to use environment variables from CloudUnit
* ${CU_DATABASE_DNS_1}
* ${CU_DATABASE_NAME}
* ${CU_DATABASE_USER_1}
* ${CU_DATABASE_PASSWORD_1}
These are injected into Server context so you can use them. 
With Spring, you need to activate a place-holder to gather them.

## Maven Packaging
```
mvn clean package -DskipTests
```
## Web UI

## Shell

