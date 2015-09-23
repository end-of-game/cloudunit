<VirtualHost *:80>
ServerName domainNameToChange

ServerAdmin webmaster@localhost
ErrorLog /var/log/apache2/git_apache_error.log
 
SetEnv GIT_PROJECT_ROOT /cloudunit/git
SetEnv GIT_HTTP_EXPORT_ALL
SetEnv REMOTE_USER=$REDIRECT_REMOTE_USER

PassEnv CU_USER
PassEnv CU_PASSWORD
PassEnv CU_REST_IP
PassEnv CU_GIT_HOME
PassEnv CU_USER_CONFIG
PassEnv CU_SERVERS_IP
PassEnv JAVA_HOME



ScriptAlias /cloudunit/git/ /usr/lib/git-core/git-http-backend/
 
<Directory "/usr/lib/git-core/">
  AllowOverride None
  Options +ExecCGI -Includes
  Order allow,deny
  Allow from all
</Directory>

<LocationMatch "^/cloudunit/git/.*$">
  AuthType Basic
  AuthName "My Git Repositories"
  AuthUserFile /etc/gitdata/gitusers.passwd
  Require valid-user

</LocationMatch>


</VirtualHost>
