# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

	config.vm.provider "virtualbox" do |vb|
    	# vb.customize ["modifyvm", :id, "--memory", "4096", "--cpus", "2"]
		vb.customize ["modifyvm", :id, "--memory", "8092", "--cpus", "2"]
	end

	config.vm.network "private_network", ip: "192.168.50.4"

	config.vbguest.auto_update = true
	config.vbguest.no_remote = false

	config.vm.box = "ubuntu/trusty64"

	config.vm.synced_folder "./", "/home/vagrant/cloudunit"

end
