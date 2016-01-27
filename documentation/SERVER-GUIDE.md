# CloudUnit server installation

You are reading the wright guide if you want to setup a CloudUnit server, in order to frequently use it. If you simply wish to test CloudUnit, you should maybe run our [Demo Vagrant box](DEMO-GUIDE.md).

## Requirements

* A server or a virtual machine with debian 7.8, and at least 16 Go of RAM.

## Installation from another host

In this part, we assume you can ssh into the server. It will be installed from your machine, using [Ansible](http://www.ansible.com/). Ansible will ssh into the server and run all the commands it needs.
Unless specified, all the commands are to be run on your machine, and not on the server.

### Pre-requisites
#### On your local host
* Install Ansible 1.9+ and sshpass. On Ubuntu:
```bash
$ sudo pip install markupsafe
$ sudo pip install ansible
$ sudo apt-get install sshpass
```

* Clone the repository [CU-infrastructure](https://github.com/Treeptik/CU-infrastructure):
```
git clone https://github.com/Treeptik/CU-infrastructure.git
```

#### On the server
* Create a user called `admincu` and add it to the sudo group:
```
# sudo adduser admincu
# sudo adduser admincu sudo
```

### CloudUnit

**On your local host**

* Change directory into `CU-infrastrucuture` and edit the `hosts` file.
In the line `cu-engine ansible_ssh_host=server_ip_address`, replace `server_ip_address` by the server IP address.

* Launch the 3 manual playbooks to install cloudunit in CU-infrastructure : 
```
ansible-playbook -vvv -i hosts --ask-pass --ask-sudo-pass playbooks/manual/installCU1.yml
SSH password:
SUDO password[defaults to SSH password]:
PLAY [cu-engine] ***********************************************************
ansible-playbook -vvv -i hosts --ask-pass --ask-sudo-pass playbooks/manual/installCU2.yml
SSH password:
SUDO password[defaults to SSH password]:
PLAY [cu-engine] ***********************************************************
ansible-playbook -vvv -i hosts --ask-pass --ask-sudo-pass playbooks/manual/installCU3.yml
SSH password:
SUDO password[defaults to SSH password]:
PLAY [cu-engine] ***********************************************************
```
Ansible prompts you for the ssh and the sudo passwords of the `admincu` user. In cas of error, due to a network problem for example, do not hesitate to relaunch the playbook. Ansible is able not to execute again a task when the desired state is already achieved.

### Run Cloudunit

You must go on the server where cloudunit has been installed and add in /etc/hosts the following line if it is not present: 
```
127.0.0.1 cloudunit.serv cu-engine
```

Then, go to ~/cloudunit/cu-manager, compile the application and send in into the tomcat server : 
```
cd ~/cloudunit/cu-manager
./compile-root-war.sh
cp ~/cloudunit/cu-manager/target/ROOT.war ~/cloudunit/cu-platform/tomcat/ROOT.war
```
Reset the platform :

```
cd ~/cloudunit/cu-platform
./reset-all.sh -y
```

### Access

You can access your cloudunit web platform on https://server_ip_address (certificates will be invalide but you can access it).

###Local DNS entry

On your local host

Finally, add a local DNS entry on your host: any address ending with cu-engine.cloudunit.io shoud point to the ip address of your server. Refere to the Local DNS section to see how to achieve this on Ubuntu.

###NB : For OVH Machine

In /etc/cloud/cloud.cfg, change manage_etc_hosts value to false

## KVM Exception

You **may** have a strange problem with KVM virtual machine.
For more information, you can read : https://blog.pivotal.io/pivotal-cloud-foundry/features/challenges-with-randomness-in-multi-tenant-linux-container-platforms

If all java applications are slow to start, you need to add `CU_KVM=true` to the `/etc/environment` file.
You need to `cloudunit/cu-platform/reset-all.sh -y`. 

It is not a mandatory action. To do only if you meet this problem. Do not hesitate to contact us.

