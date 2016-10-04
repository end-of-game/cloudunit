INSERT IGNORE INTO `Role` (`id`, `description`) VALUES
  (1, 'ROLE_ADMIN'),
  (2, 'ROLE_USER');

INSERT IGNORE INTO `Image`
    (`id`, `name`, `path`, `displayName`, `prefixEnv`, `imageType`, `managerName`, `imageSubType`) VALUES
  (10, 'tomcat-6', 'cloudunit/tomcat-6', 'Tomcat 6.0.45', 'tomcat', 'server', '', null),
  (11, 'tomcat-7', 'cloudunit/tomcat-7', 'Tomcat 7.0.70', 'tomcat', 'server', '',  null),
  (12, 'tomcat-8', 'cloudunit/tomcat-8', 'Tomcat 8.0.37', 'tomcat', 'server', '', null),
  (13, 'tomcat-85', 'cloudunit/tomcat-85', 'Tomcat 8.5.5', 'tomcat', 'server', '', null),
  (14, 'tomcat-9', 'cloudunit/tomcat-9', 'Tomcat 9.0.0.M10', 'tomcat', 'server', '', null),
  (20, 'mysql-5-5', 'cloudunit/mysql-5-5', 'MySQL 5.5.49', 'mysql', 'module', 'phpmyadmin', 'DATABASE'),
  (21, 'mysql-5-6', 'cloudunit/mysql-5-6', 'MySQL 5.6.30', 'mysql', 'module', 'phpmyadmin', 'DATABASE'),
  (22, 'mysql-5-7', 'cloudunit/mysql-5-7', 'MySQL 5.7.12', 'mysql', 'module', 'phpmyadmin', 'DATABASE'),
  (30, 'mongo-2-6', 'cloudunit/mongo-2-6', 'Mongo 2.6', 'mongo', 'module', 'mms', 'DATABASE'),
  (40, 'redis-2-8', 'cloudunit/redis-2-8', 'Redis 2.8.24', 'redis', 'module', 'redmin', 'DATAGRID'),
  (41, 'redis-3-0', 'cloudunit/redis-3-0', 'Redis 3.0.7', 'redis', 'module', 'redmin', 'DATAGRID'),
  (50, 'postgresql-9-3', 'cloudunit/postgresql-9-3', 'PostgreSQL 9.3.12', 'postgresql', 'module', 'phppgadmin', 'DATABASE'),
  (51, 'postgresql-9-4', 'cloudunit/postgresql-9-4', 'PostgreSQL 9.4.7', 'postgresql', 'module', 'phppgadmin', 'DATABASE'),
  (52, 'postgresql-9-5', 'cloudunit/postgresql-9-5', 'PostgreSQL 9.5.2', 'postgresql', 'module', 'phppgadmin', 'DATABASE'),
  (53, 'postgis-2-2', 'cloudunit/postgis-2-2', 'PostGIS 2.2.1 (9.4)', 'postgresql', 'module', 'phppgadmin','DATABASE'),
  (60, 'wildfly-8', 'cloudunit/wildfly-8', 'WildFly 8.2.1', 'wildfly', 'server', '',null),
  (61, 'wildfly-9', 'cloudunit/wildfly-9', 'WildFly 9.0.2', 'wildfly', 'server', '', null),
  (62, 'wildfly-10', 'cloudunit/wildfly-10', 'WildFly 10.0.0', 'wildfly', 'server', '',null),
  (70, 'fatjar', 'cloudunit/fatjar', 'FatJar', 'fatjar', 'server', '',null),
  (80, 'apache-2-2', 'cloudunit/apache-2-2', 'Apache 2.2.22', 'apache', 'server', '', null),
  (90, 'activemq-5.13.2', 'cloudunit/activemq-5.13.2', 'ActiveMQ 5.13.2', 'activemq', 'module', '', 'MESSAGE_BROKER'),
  (100, 'rabbitmq-3.6.5-1', 'cloudunit/rabbitmq-3.6.5-1', 'RabbitMQ 3.6.5-1', 'rabbitmq', 'module', '', 'MESSAGE_BROKER');

INSERT IGNORE INTO `Image_moduleEnvironmentVariables` (`moduleEnvironmentVariables`,`moduleEnvironmentVariables_KEY`,`Image_id`)
VALUES
    ("MYSQL_USER", "USER", 20),
    ("MYSQL_PASSWORD", "PASSWORD", 20),
    ("MYSQL_DATABASE", "NAME", 20),

    ("MYSQL_USER", "USER", 21),
    ("MYSQL_PASSWORD", "PASSWORD", 21),
    ("MYSQL_DATABASE", "NAME", 21),

    ("MYSQL_USER", "USER", 22),
    ("MYSQL_PASSWORD", "PASSWORD", 22),
    ("MYSQL_DATABASE", "NAME", 22),

    ("POSTGRES_USER", "USER", 50),
    ("POSTGRES_PASSWORD", "PASSWORD", 50),
    ("POSTGRES_DB", "NAME", 50),

    ("POSTGRES_USER", "USER", 51),
    ("POSTGRES_PASSWORD", "PASSWORD", 51),
    ("POSTGRES_DB", "NAME", 51),

    ("POSTGRES_USER", "USER", 52),
    ("POSTGRES_PASSWORD", "PASSWORD", 52),
    ("POSTGRES_DB", "NAME", 52),

    ("POSTGRES_USER", "USER", 53),
    ("POSTGRES_PASSWORD", "PASSWORD", 53),
    ("POSTGRES_DB", "NAME", 53),

    ("ACTIVEMQ_ADMIN_LOGIN", "USER", 90),
    ("ACTIVEMQ_ADMIN_PASSWORD", "PASSWORD", 90),
    ("ACTIVEMQ_NAME", "NAME", 90),

    ("RABBITMQ_DEFAULT_USER", "USER", 100),
    ("RABBITMQ_DEFAULT_PASS", "PASSWORD", 100),
    ("RABBITMQ_DEFAULT_VHOST", "NAME", 100);


INSERT IGNORE INTO `Image_exposedPorts` (`exposedPorts`,`exposedPorts_KEY`,`Image_id`)
VALUES
-- mysql ports values --

("3306", "TCP", 20),
("3306", "TCP", 21),
("3306", "TCP", 22),

-- postgres & postgis ports values --

("5432", "TCP", 50),
("5432", "TCP", 51),
("5432", "TCP", 52),
("5432", "TCP", 53),

-- activemq ports values --

("8161", "WEBCONSOLE", 90),
("61616", "OPENWIRE", 90),
("5672", "AMQP", 90),
("1883", "MQTT", 90),
("61613", "STOMP", 90),
("61614", "WEBSERVICES", 90),

-- rabbitmq ports values --  

("15672", "WEBCONSOLE", 100),
("5672", "AMQP", 100);


INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (1, 'John', 'Doe', 'johndoe.doe@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'johndoe',
   'admin');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (2, 'scott', 'tiger', 'scott.tiger@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2014-02-22 09:22:06', 'scott',
   'user');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (3, 'user1', 'test1', 'usertest1@treeptik.fr', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'usertest1',
   'user');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (4, 'user2', 'test2', 'usertest2@treeptik.fr', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'usertest2',
   'user');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (5, 'user3', 'test3', 'usertest3@treeptik.fr', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'usertest3',
   'user');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (6, 'user4', 'test4', 'usertest4@treeptik.fr', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'usertest4',
   'user');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (7, 'user5', 'test5', 'usertest5@treeptik.fr', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'usertest5',
   'user');
INSERT IGNORE INTO `Metric` (`id`, `name`, `url`, `serverName`, `suffix`) VALUES (1, "memoryHeap", "java.lang:type=Memory/HeapMemoryUsage", "all", "used")
   ,(2, "threadCount", "java.lang:type=Threading", "all", "ThreadCount"), (3, "currentThreadsBusy", 'Catalina:name="http-bio-8080",type=ThreadPool', "tomcat", "currentThreadsBusy") ;

