INSERT IGNORE INTO `Role` (`id`, `description`) VALUES
  (1, 'ROLE_ADMIN'),
  (2, 'ROLE_USER');

INSERT IGNORE INTO `Image` (`id`, `name`, `path`, `version`, `cmd`, `status`, `imageType`, `managerName`) VALUES
  (1, 'tomcat-6', 'cloudunit/tomcat-appconf6', '.0.41', '', 1, 'server', ''),
  (2, 'tomcat-7', 'cloudunit/tomcat-appconf7', '.0.47', '', 1, 'server', ''),
  (3, 'tomcat-8', 'cloudunit/tomcat-appconf8', '.0.14', '', 1, 'server', ''),
  (4, 'jboss-8', 'cloudunit/jboss-appconf8', '1.0', '', 1, 'server', ''),
  (5, 'fatjar', 'cloudunit/fatjar', '1.0', '', 1, 'server', ''),
  (7, 'mysql-5-5', 'cloudunit/mysql-5-5', '1.0', '', 1, 'module', 'phpmyadmin'),
  (10, 'postgresql-9-3', 'cloudunit/postgresql-9-3', '1.0', '', 1, 'module', 'phppgadmin'),
  (11, 'mongo-2-6', 'cloudunit/mongo-2-6', '1.0', '', 1, 'module', 'mms'),
  (12, 'postgis-2-2', 'cloudunit/postgis-2-2', '1.0', '', 1, 'module', 'phppgadmin'),
  (13, 'redis-3-0', 'cloudunit/redis-3-0', '1.0', '', 1, 'module', 'redmin');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (1, 'John', 'Doe', 'johndoe.doe@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2013-08-22 09:22:06', 'johndoe',
   'admin');

INSERT IGNORE INTO `User` (`id`, `firstName`, `lastName`, `email`, `password`, `role_id`, `status`, `signin`, `login`, `organization`)
VALUES
  (2, 'scott', 'tiger', 'scott.tiger@gmail.com', 'cVwsWoHVZ28Qf9fHE0W4Qg==', 1, 1, '2014-02-22 09:22:06', 'scott',
   'user');

