INSERT IGNORE INTO `Role` (`id`, `description`) VALUES
  (1, 'ROLE_ADMIN'),
  (2, 'ROLE_USER');

INSERT IGNORE INTO `Image` (`id`, `name`, `path`, `displayName`, `status`, `imageType`, `managerName`) VALUES
  (1, 'tomcat-6', 'cloudunit/tomcat-appconf6', 'Tomcat 6.0.41', 1, 'server', ''),
  (2, 'tomcat-7', 'cloudunit/tomcat-appconf7', 'Tomcat 7.0.47', 1, 'server', ''),
  (3, 'tomcat-8', 'cloudunit/tomcat-appconf8', 'Tomcat 8.0.14', 1, 'server', ''),
  (4, 'jboss-8', 'cloudunit/jboss-appconf8', 'jBoss 8', 1, 'server', ''),
  (5, 'fatjar', 'cloudunit/fatjar', 'FatJar', 1, 'server', ''),
  (10, 'mysql-5-5', 'cloudunit/mysql-5-5', 'MySQL 5.5.49', 1, 'module', 'phpmyadmin'),
  (11, 'mysql-5-6', 'cloudunit/mysql-5-6', 'MySQL 5.6.30', 1, 'module', 'phpmyadmin'),
  (12, 'mysql-5-7', 'cloudunit/mysql-5-7', 'MySQL 5.7.12', 1, 'module', 'phpmyadmin'),
  (20, 'mongo-2-6', 'cloudunit/mongo-2-6', 'Mongo 2.6', 1, 'module', 'mms'),
  (30, 'redis-2-8', 'cloudunit/redis-2-8', 'Redis 2.8.23', 1, 'module', 'redmin'),
  (31, 'redis-3-0', 'cloudunit/redis-3-0', 'Redis 3.0.7', 1, 'module', 'redmin'),
  (40, 'postgresql-9-3', 'cloudunit/postgresql-9-3', 'PostgreSQL 9.3.12', 1, 'module', 'phppgadmin'),
  (41, 'postgresql-9-4', 'cloudunit/postgresql-9-4', 'PostgreSQL 9.4.7', 1, 'module', 'phppgadmin'),
  (42, 'postgresql-9-5', 'cloudunit/postgresql-9-5', 'PostgreSQL 9.5.2', 1, 'module', 'phppgadmin'),
  (43, 'postgresql-9-4-2-2', 'cloudunit/postgis-2-2', 'PostGIS 2.2.1 (9.4)', 1, 'module', 'phppgadmin');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (1, 'John', 'Doe', 'johndoe.doe@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'johndoe',
   'admin');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (2, 'scott', 'tiger', 'scott.tiger@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2014-02-22 09:22:06', 'scott',
   'user');

