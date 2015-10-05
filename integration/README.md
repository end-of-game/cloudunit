# SIMPLIFY THE PROCESSUS WITH PUBLIC KEYS

Rather than use vagrant/vagrant at each deployment, use a public key and copy it to the vagrant box

From your local machine 
* *ssh-keygen* (create a 'vagrant' entry)
* *ssh-copy-id vagrant@192.168.50.4* (password: vagrant)

# LIST OF THE SCRIPTS

These are differents scripts files for integration tests.
* *integration.sh* is the complete stack for testing integration. Use it with maven.
* *aliases.sh* : Testing alias features
* *security.sh* : Testing security feature to valid an user cannot access to others resources
