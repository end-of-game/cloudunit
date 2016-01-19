#!/bin/bash

export ENV_FILE="/etc/environment"

# Ajout des variables d'environnement
export CU_USER=$1
export CU_PASSWORD=$2
# needed for gitpush
export CU_REST_IP=$3
export CU_SERVERS_IP=$4
export CU_APP_NAME=$5
# Database password for Manager
export MANAGER_DATABASE_PASSWORD=$6
# To do difference between main and test env
export ENV_EXEC=$7
export CU_DATABASE_DNS=$8

# ENVOI NOTIFICATION CHANGEMENT DE STATUS
if [ $ENV_EXEC = "integration" ];
then
    export MYSQL_ENDPOINT=cuplatform_testmysql_1.mysql.cloud.unit
else
    export MYSQL_ENDPOINT=$CU_DATABASE_DNS
fi


if [ ! -f /init-service-ok ]; then

    #################
    # PREMIER APPEL #
    #################
    echo "Start Services and configure password for $1"
    /usr/sbin/sshd

    #Transforme les variables en variables d'environnements
    echo -n "CU_USER=$CU_USER\n" >> $ENV_FILE
    echo -n "CU_PASSWORD=$CU_PASSWORD\n" >> $ENV_FILE
    echo -n "CU_REST_IP=$CU_REST_IP\n" >> $ENV_FILE
    echo -n "CU_SERVERS_IP=$CU_SERVERS_IP\n" >> $ENV_FILE
    echo -n "CU_APP_NAME=$CU_APP_NAME\n" >> $ENV_FILE

    # Ajout de l'utilisateur et modif du home directory
    useradd $1 && echo "$CU_USER:$CU_PASSWORD" | chpasswd && echo "root:$CU_PASSWORD" | chpasswd

    # GENERATION CLES SSH POUR LIEN AVEC MODULES
    ssh-keygen -t rsa -N "" -f /root/.ssh/id_rsa
    mkdir -p $CU_USER_HOME/.ssh
    cp /root/.ssh/id_rsa.pub $CU_USER_HOME/.ssh
    cp /root/.ssh/id_rsa $CU_USER_HOME/.ssh
    cp /root/.bashrc $CU_USER_HOME
    cp /root/.profile $CU_USER_HOME

    # Affection du homedirectory �|  l'utilisateur unix
    usermod -d $CU_USER_HOME $1

    # Incremente le nombre de push git
    echo 0 > $CU_USER_CONFIG/countGitPush

    # Ajout et configuration du repo GIT
    touch $CU_USER_HOME/.gitconfig
    echo "[user]\n\tname = $CU_USER\n[core]\n\teditor = vim" > $CU_USER_HOME/.gitconfig
    cd $CU_GIT_HOME && git init && git config core.bare true && git update-server-info

    mv /cloudunit/scripts/post-receive $CU_GIT_HOME/.git/hooks/post-receive
    mv /cloudunit/scripts/post-merge $CU_GIT_HOME/.git/hooks/post-merge

    # Ajout du Shell �|  l'utilisateur
    usermod -s /bin/bash $CU_USER

    # Configuration et démarrage d'Apache
    mv /cloudunit/scripts/cu_gitdomain.com /etc/apache2/sites-available/cu_gitdomain.com
    mkdir /etc/gitdata
    htpasswd -bc /etc/gitdata/gitusers.passwd $CU_USER $CU_PASSWORD
    sed -i 's/domainNameToChange/'$CU_APP_NAME'-'$1'.cloudunit.io/g' /etc/apache2/sites-available/cu_gitdomain.com
    sed -i 's/export APACHE_RUN_USER=www-data/export APACHE_RUN_USER='$1'/g' /etc/apache2/envvars
    sed -i 's/export APACHE_RUN_GROUP=www-data/export APACHE_RUN_GROUP='$1'/g' /etc/apache2/envvars
    a2enmod ssl cgi alias env
    a2ensite cu_gitdomain.com

    chown -R $CU_USER:$CU_USER /var/www

    # Utile pour le second appel
    touch /init-service-ok
fi

# Démarrage de rsyslog, ssh et apache
#rsyslogd
/usr/sbin/sshd

. /etc/apache2/envvars && /usr/sbin/apache2ctl start
# Attente du démarrage du processus sshd pour confirmer au manager
until [ "`nc -z localhost 22 && echo $?`" -eq "0" ]
do	
    echo -n -e "\nwaiting for sshd process start";
    sleep 1
done

# Le montage /cloudunit n'appartient qu'a|  l'utilisateur créé
chown -R $CU_USER:$CU_USER /cloudunit
echo /cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME START $MANAGER_DATABASE_PASSWORD
/cloudunit/java/jdk1.7.0_55/bin/java -jar /cloudunit/tools/cloudunitAgent-1.0-SNAPSHOT.jar MODULE $MYSQL_ENDPOINT $HOSTNAME START $MANAGER_DATABASE_PASSWORD

touch /cloudunit/scripts/done.txt

tailf /var/log/faillog
