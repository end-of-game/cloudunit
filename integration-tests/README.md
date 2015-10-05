# SIMPLIFY THE PROCESSUS WITH PUBLIC KEYS

Rather than use vagrant/vagrant at each deployment, use a public key and copy it to the vagrant box

From your local machine 
* **ssh-keygen** (create a 'vagrant' entry)
* **ssh-copy-id vagrant@192.168.50.4** (password: vagrant)

# SCRIPTS

> ALWAYS GO TO SUB-DIRECTORY TO EXECUTE THEM

These are differents scripts files for integration tests.
* **integration.sh** is the complete stack for testing integration. Use it with maven.
* **aliases.sh** : Testing alias features
* **deployments.sh** : Testing deployments for all servers and module
* **deployment6.sh** : Testing deployment for Tomcat 6
* **deployment7.sh** : Testing deployment for Tomcat 7
* **deployment8.sh** : Testing deployment for Tomcat 8
* **modules.sh** : Testing all modules
* **security.sh** : Testing security feature to valid an user cannot access to others resources
* **tomcats.sh** : Testing all tomcat server
* **tomcat6.sh** : Testing Tomcat 6
* **tomcat8.sh** : Testing Tomcat 8
* **tomcat7.sh** : Testing Tomcat 7

> Warning : Tasks could be very time consuming

 
