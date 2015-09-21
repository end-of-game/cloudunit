# CloudUnit server installation

You are reading the wright guide if you want to setup a CloudUnit server, in order to frequently use it. If you simply wish to test CloudUnit, you should maybe run our [Demo Vagrant box](https://github.com/Treeptik/CloudUnit/blob/master/DEMO-GUIDE.md). If you want to setup an environment to contribute to CloudUnit development, read our [Dev guide](https://github.com/Treeptik/cloudunit/blob/master/DEV-GUIDE.md).


## Requirements

* A server or a virtual machine with debian 7.8, and at least 16 Go of RAM.

## Installation from another host

In this part, we assume you can ssh into the server. It will be installed from your machine, using [Ansible](http://www.ansible.com/). Ansible will ssh into the server and run all the commands it needs.
Unless specified, all the commands are to be run on your machine, and not on the server.

### Pre-requisites

* Install Ansible 1.9+. On Ubuntu:
```bash
sudo pip install markupsafe
sudo pip install ansible
```

* Clone the repository [CU-infrastructure](https://github.com/Treeptik/CU-infrastructure):
```
git clone https://github.com/Treeptik/CU-infrastructure.git
```

* **On the server**, create a user called `admincu` and add it to the sudo I group:
```
sudo adduser admincu
sudo adduser admincu sudo
```

### CloudUnit

* Change directory into `CU-infrastrucuture` and edit the `hosts` file.
In the line `CUserver ansible_ssh_host=server_ip_address`, replace `server_ip_address` by the server IP address.

* *If your server is a KVM virtual machine*, edit the file `installCUserver.yml`. Replace `kvm: false` by `kvm: true`.

* Launch the CloudUnit installation playbook:
```
ansible-playbook -vvv -i hosts --ask-pass --ask-sudo-pass installCUserver.yml
SSH password:
SUDO password[defaults to SSH password]:
 
PLAY [CUserveur] ***********************************************************
```
Ansible prompts you for the ssh and the sudo passwords of the `admincu` user. In cas of error, due to a network problem for example, do not hesitate to relaunch the playbook. Ansible is able not to execute again a task when the desired state is already achieved.


### Shinken

Our PaaS is monitored using [Shinken](http://www.shinken-monitoring.org/). Because it contributes to the stability of our platform, we strongly recommend you to install it as well.

* *On the server*, create a user called `shinken` and add it to the sudo group:
```
sudo adduser shinken
sudo adduser shinken sudo
```

* Launch the Shinken installation playbook:
```
ansible-playbook -vvv -i hosts --ask-pass --ask-sudo-pass shinken-standalone.yml
SSH password:
SUDO password[defaults to SSH password]:
 
PLAY [CUserveur] ***********************************************************
```
You are prompted for the ssh and sudo passwords of the `shinken` user.
