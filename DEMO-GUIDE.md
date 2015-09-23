# Demo Vagrant box

This guide is relevant if you want to try CloudUnit without going through any complicated installation procedure. We provide a Vagrant box with CloudUnit version 1.0.

## Requirements
* Virtualbox version 5.0.4+
* Vagrant plugin ?

## Start the box
You can launch it in 2 commands:
```
$ vagrant init treeptik/CUdemo
$ vagrant up
```

The default box IP address is 192.168.50.4, but if you want it to be reachable on the network, uncomment the line `config.vm.network "public_network"` in the `Vagrantfile`.

## Enjoy CloudUnit
CloudUnit WebUI is available at the box IP. You can login with the credentials johndoe / abc2015.

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/CU-login.png)

## CloudUnit Menu

When you are connected in the CloudUnit application, the menu is everytime visible on the top of the application. You navigate in the application with this header menu.

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/cu-header.png)

The first tab of the menu gets to the dashboard and the second to the shapshots. The two button of the right give access the account page and the deconnection. 

## CloudUnit Dashboard

After your login, you are redirected to the dashboard of CloudUnit.

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/cu-dashboard.png)

The dashboard lists all yours applications in CloudUnit. You can add, manage and remove a application in the dashboard. You can filter by status in the list of applications.
The tasks timeline displays all actions in your CloudUnit environment.

### Create a application in CloudUnit

To create a application, you need to fill the name of your application and select it server in the dashboard form.

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/cu-dashboard-add.png)

After click to the submit button "Create New App", your new application is added to the list of applications and the tasks timeline puts this new action up. 

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/cu-dashboard-add-submit.png)

### Remove a application in CloudUnit

To remove your application, you click in the trash icon of this application. A popup of validation posts in the center of your screen. 

![login](https://github.com/Treeptik/CloudUnit-images/blob/master/cu-dashboard-remove.png)

If you accept this popup, CloudUnit deletes this application.
