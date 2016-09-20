INSERT IGNORE INTO `Role` (`id`, `description`) VALUES
  (1, 'ROLE_ADMIN'),
  (2, 'ROLE_USER');

INSERT IGNORE INTO `Image` (`id`, `name`, `path`, `displayName`, `prefixEnv`, `imageType`, `managerName`, `exposedPort`) VALUES
  (10, 'tomcat-6', 'cloudunit/tomcat-6', 'Tomcat 6.0.45', 'tomcat', 'server', '', ''),
  (11, 'tomcat-7', 'cloudunit/tomcat-7', 'Tomcat 7.0.70', 'tomcat', 'server', '', ''),
  (12, 'tomcat-8', 'cloudunit/tomcat-8', 'Tomcat 8.0.37', 'tomcat', 'server', '', ''),
  (13, 'tomcat-85', 'cloudunit/tomcat-85', 'Tomcat 8.5.5', 'tomcat', 'server', '', ''),
  (14, 'tomcat-9', 'cloudunit/tomcat-9', 'Tomcat 9.0.0.M10', 'tomcat', 'server', '', ''),
  (20, 'mysql-5-5', 'cloudunit/mysql-5-5', 'MySQL 5.5.49', 'mysql', 'module', 'phpmyadmin', '3306'),
  (21, 'mysql-5-6', 'cloudunit/mysql-5-6', 'MySQL 5.6.30', 'mysql', 'module', 'phpmyadmin', '3306'),
  (22, 'mysql-5-7', 'cloudunit/mysql-5-7', 'MySQL 5.7.12', 'mysql', 'module', 'phpmyadmin', '3306'),
  (30, 'mongo-2-6', 'cloudunit/mongo-2-6', 'Mongo 2.6', 'mongo', 'module', 'mms', ''),
  (40, 'redis-2-8', 'cloudunit/redis-2-8', 'Redis 2.8.24', 'redis', 'module', 'redmin', ''),
  (41, 'redis-3-0', 'cloudunit/redis-3-0', 'Redis 3.0.7', 'redis', 'module', 'redmin', ''),
  (50, 'postgresql-9-3', 'cloudunit/postgresql-9-3', 'PostgreSQL 9.3.12', 'postgresql', 'module', 'phppgadmin', '5432'),
  (51, 'postgresql-9-4', 'cloudunit/postgresql-9-4', 'PostgreSQL 9.4.7', 'postgresql', 'module', 'phppgadmin', '5432'),
  (52, 'postgresql-9-5', 'cloudunit/postgresql-9-5', 'PostgreSQL 9.5.2', 'postgresql', 'module', 'phppgadmin', '5432'),
  (53, 'postgis-2-2', 'cloudunit/postgis-2-2', 'PostGIS 2.2.1 (9.4)', 'postgresql', 'module', 'phppgadmin', '5432'),
  (60, 'wildfly-8', 'cloudunit/wildfly-8', 'WildFly 8.2.1', 'wildfly', 'server', '', ''),
  (61, 'wildfly-9', 'cloudunit/wildfly-9', 'WildFly 9.0.2', 'wildfly', 'server', '', ''),
  (62, 'wildfly-10', 'cloudunit/wildfly-10', 'WildFly 10.0.0', 'wildfly', 'server', '', ''),
  (70, 'fatjar', 'cloudunit/fatjar', 'FatJar', 'fatjar', 'server', '', ''),
  (80, 'apache-2-2', 'cloudunit/apache-2-2', 'Apache 2.2.22', 'apache', 'server', '', '');

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
    ("POSTGRES_DB", "NAME", 53);
RD", 50),
	("POSTGRES_DB", "NAME", 50);

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