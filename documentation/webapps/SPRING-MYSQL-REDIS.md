# SPRING MYSQL REDIS

## Goals

This guide explains how to create a JEE application with two modules:
* Mysql
* Redis

We will use the cloudunit internal mechanism based on environment variable.

## Clone the opensource project from SpringSource
```
git clone https://github.com/Treeptik/cloudunit-webapp-examples
```

## Redis

When you add your first module REDIS, you will access to these
* CU_DATABASE_DNS_REDIS_1
* CU_DATABASE_USER_REDIS_1
* CU_DATABASE_PASSWORD_REDIS_1

So in your java code, you can use them as
```
private Jedis jedis;
@PostConstruct
    public void init() {
        String redisServer = System.getenv("CU_DATABASE_DNS_REDIS_1");
        String redisUser = System.getenv("CU_DATABASE_USER_REDIS_1");
        String redisPassword = System.getenv("CU_DATABASE_PASSWORD_REDIS_1");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(jedisPoolConfig, redisServer);
        jedis = pool.getResource();
        jedis.auth(redisPassword);
    }
```

Edit the following files:

* Add into pom.xml
```
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.8.0</version>
</dependency>
```

## MYSQL

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

They are injected into Server context so you can use them. 
With Spring, you need to activate a place-holder to gather them.

## Maven Packaging
```
mvn clean package -DskipTests
```
## Web UI

## Shell

