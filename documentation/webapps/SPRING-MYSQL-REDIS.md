# SPRING MYSQL REDIS

## Goals

This guide explains how to create a JEE application with two modules:
* MySQL
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

When you add your first module REDIS, you will access to these
* CU_DATABASE_DNS_REDIS_1
* CU_DATABASE_USER_REDIS_1
* CU_DATABASE_PASSWORD_REDIS_1

So in your configuration code, you can use them as
```
 <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://${CU_DATABASE_DNS_MYSQL_1}:3306/${CU_DATABASE_NAME}"/>
    <property name="username" value="${CU_DATABASE_USER_MYSQL_1}"/>
    <property name="password" value="${CU_DATABASE_PASSWORD_MYSQL_1}"/>
    <property name="validationQuery" value="SELECT 1"/>
</bean>
```
As the same way for Redis, you could use `System.getenv` to gather them.
They are injected into Server context so you can use them. 
With Spring, you need to activate a place-holder to gather them.

Edit the following files:

* Add into pom.xml
```
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>5.1.22</version>
</dependency>
```

## Maven Packaging

You can package the example with maven and deploy it with WebUI
```
cd $HOME/cloudunit-webapp-examples/spring-mysql-redis/ && mvn clean package -DskipTests
```

## Visual Flow

### Create a new application

![CloudUnit](https://github.com/Treeptik/cloudunit/blob/redis-module-feature/documentation/img/spring-mysql-redis-01.png =250px)

### Add the modules

![CloudUnit](https://github.com/Treeptik/cloudunit/blob/redis-module-feature/documentation/img/spring-mysql-redis-02.png "Spring MYSQL REDIS 02")

### Get the informations

![CloudUnit](https://github.com/Treeptik/cloudunit/blob/redis-module-feature/documentation/img/spring-mysql-redis-03.png "Spring MYSQL REDIS 03")

You can use these informations to log into the two managers:
* phpMyAdmin
* PhpRedmin

### Deploy the war

![CloudUnit](https://github.com/Treeptik/cloudunit/blob/redis-module-feature/documentation/img/spring-mysql-redis-04.png "Spring MYSQL REDIS 04")

### Access to application

![CloudUnit](https://github.com/Treeptik/cloudunit/blob/redis-module-feature/documentation/img/spring-mysql-redis-05.png "Spring MYSQL REDIS 05")

