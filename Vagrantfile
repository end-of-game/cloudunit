# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

	config.vm.provider "virtualbox" do |vb|
		vb.customize ["modifyvm", :id, "--memory", "4096", "--cpus", "2"]
		#vb.customize ["modifyvm", :id, "--memory", "8092", "--cpus", "2"]		
	end

	config.vm.network "private_network", ip: "192.168.50.4"

	config.vbguest.auto_update = true
	config.vbguest.no_remote = false

	config.vm.box = "treeptik/debian-wheezy"

	#DEPUIS L'IMAGE DEBIAN
	config.vm.define "dev", autostart: true do |dev|

 		dev.vm.synced_folder "./", "/home/vagrant/cloudunit"

		dev.vm.provision "ansible" do |ansible|
			ansible.verbose = "vvvv"
			ansible.playbook = "../infrastructure/CU-infrastructure/playbooks/dev-vagrant/vagrant1.yml"
		end

		dev.vm.provision :reload

		dev.vm.provision "ansible" do |ansible|
			ansible.verbose = "vvvv"
			ansible.playbook = "../infrastructure/CU-infrastructure/playbooks/dev-vagrant/vagrant2.yml"
		end

		dev.vm.provision :reload

		dev.vm.provision "ansible" do |ansible|
			ansible.verbose = "vvvv"
			ansible.playbook = "../infrastructure/CU-infrastructure/playbooks/dev-vagrant/vagrant3.yml"
		end
	end	

	config.vm.define "demo", autostart: false do |demo|


		demo.vm.provision "ansible" do |ansible|
			ansible.verbose = "vvvv"
			ansible.playbook = "../infrastructure/CU-infrastructure/playbooks/demo-vagrant/demovagrant1.yml"
		end

		demo.vm.provision :reload

		demo.vm.provision "ansible" do |ansible|
			ansible.verbose = "vvvv"
			ansible.playbook = "../infrastructure/CU-infrastructure/playbooks/demo-vagrant/demovagrant2.yml"
		end

		demo.vm.provision :reload

		demo.vm.provision "ansible" do |ansible|
			ansible.verbose = "vvvv"
			ansible.playbook = "../infrastructure/CU-infrastructure/playbooks/demo-vagrant/demovagrant3.yml"
		end

	end	


end
