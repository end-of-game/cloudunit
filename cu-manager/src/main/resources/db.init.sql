INSERT IGNORE INTO `Role` (`id`, `description`) VALUES
  (1, 'ROLE_ADMIN'),
  (2, 'ROLE_USER');

INSERT IGNORE INTO `Image` (`id`, `name`, `path`, `displayName`, `prefixEnv`, `imageType`, `managerName`) VALUES
  (1, 'tomcat-6', 'cloudunit/tomcat-6', 'Tomcat 6.0.45', 'tomcat', 'server', ''),
  (2, 'tomcat-7', 'cloudunit/tomcat-7', 'Tomcat 7.0.70', 'tomcat', 'server', ''),
  (3, 'tomcat-8', 'cloudunit/tomcat-8', 'Tomcat 8.0.36', 'tomcat', 'server', ''),
  (4, 'tomcat-85', 'cloudunit/tomcat-85', 'Tomcat 8.5.4', 'tomcat', 'server', ''),
  (5, 'tomcat-9', 'cloudunit/tomcat-8', 'Tomcat 9.0.0.M9', 'tomcat', 'server', ''),
  (5, 'fatjar', 'cloudunit/fatjar', 'FatJar', 'fatjar', 'server', ''),
  (6, 'apache-2-2', 'cloudunit/apache-2-2', 'Apache 2.2.22', 'apache', 'server', ''),
  (10, 'mysql-5-5', 'cloudunit/mysql-5-5', 'MySQL 5.5.49', 'mysql', 'module', 'phpmyadmin'),
  (11, 'mysql-5-6', 'cloudunit/mysql-5-6', 'MySQL 5.6.30', 'mysql', 'module', 'phpmyadmin'),
  (12, 'mysql-5-7', 'cloudunit/mysql-5-7', 'MySQL 5.7.12', 'mysql', 'module', 'phpmyadmin'),
  (20, 'mongo-2-6', 'cloudunit/mongo-2-6', 'Mongo 2.6', 'mongo', 'module', 'mms'),
  (30, 'redis-2-8', 'cloudunit/redis-2-8', 'Redis 2.8.24', 'redis', 'module', 'redmin'),
  (31, 'redis-3-0', 'cloudunit/redis-3-0', 'Redis 3.0.7', 'redis', 'module', 'redmin'),
  (40, 'postgresql-9-3', 'cloudunit/postgresql-9-3', 'PostgreSQL 9.3.12', 'postgresql', 'module', 'phppgadmin'),
  (41, 'postgresql-9-4', 'cloudunit/postgresql-9-4', 'PostgreSQL 9.4.7', 'postgresql', 'module', 'phppgadmin'),
  (42, 'postgresql-9-5', 'cloudunit/postgresql-9-5', 'PostgreSQL 9.5.2', 'postgresql', 'module', 'phppgadmin'),
  (43, 'postgis-2-2', 'cloudunit/postgis-2-2', 'PostGIS 2.2.1 (9.4)', 'postgresql', 'module', 'phppgadmin'),
  (50, 'wildfly-8', 'cloudunit/wildfly-8', 'WildFly 8.2.1', 'wildfly', 'server', ''),
  (51, 'wildfly-9', 'cloudunit/wildfly-9', 'WildFly 9.0.2', 'wildfly', 'server', ''),
  (52, 'wildfly-10', 'cloudunit/wildfly-10', 'WildFly 10.0.0', 'wildfly', 'server', '');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (1, 'John', 'Doe', 'johndoe.doe@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'johndoe',
   'admin');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (2, 'scott', 'tiger', 'scott.tiger@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 2, 1, '2014-02-22 09:22:06', 'scott',
   'user');
   INSERT IGNORE INTO `Metric` (`id`, `name`, `url`, `serverName`, `suffix`) VALUES (1, "memoryHeap", "java.lang:type=Memory/HeapMemoryUsage", "all", "used")
   ,(2, "threadCount", "java.lang:type=Threading", "all", "ThreadCount"), (3, "currentThreadsBusy", 'Catalina:name="http-bio-8080",type=ThreadPool', "tomcat", "currentThreadsBusy") ;

