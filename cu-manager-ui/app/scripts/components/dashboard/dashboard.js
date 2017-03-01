/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

(function () {
  'use strict';

  /**
   * @ngdoc function
   * @name webuiApp.controller:MainCtrl
   * @description
   * # MainCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .component('dashboard', Dashboard());

  function Dashboard(){
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/dashboard/dashboard.html',
      bindings: {},
      controller: [
        '$rootScope',
        '$scope',
        '$interval',
        'ApplicationService',
        'ErrorService',
        DashboardCtrl
      ],
      controllerAs: 'dashboard',
    };
  }

  function DashboardCtrl($rootScope, $scope, $interval, ApplicationService, ErrorService) {
    var timer, vm = this;
    vm.applications = [];
    vm.selectedItem = 'All';
    vm.selectedServerSearch = 'All';
    vm.selectedServer = '';
    vm.search = '';
    vm.deleteApplication = deleteApplication;
    vm.toggleServer = toggleServer;
    vm.buffer = '';
    vm.selectedDisplayStyle = 'Grid';
    vm.applicationsSelectedAction = [];
    vm.selectedAction = selectedAction;
    vm.deleteActions = deleteActions;
    vm.stopActions = stopActions;
    vm.startActions = startActions;

    vm.actions = {
      stop: true,
      start: true
    };
    // vm.checkCancel = checkCancel;
    update();

    // Polling on refresh
    timer = $interval(function () {
      update();
    }, 2000);


    vm.$onDestroy = function () {
      $interval.cancel(timer);
    };
  
    $scope.$on('app:creating', function(e, data){
      vm.buffer = data;
    });

    $scope.$on('app:create:fail', function(e, data){
      vm.buffer = '';
    });
    
    $scope.$on('app:serverImages', function(event, args) {
      vm.serverImages = args.serverImages;
    });
    
    /////////////////////////////////////////////

    // Refresh the application list
    function update() {
      ApplicationService.list()
        .then(success)
        .catch(error);

      function success(applications) {

        var newApp = _.find(applications, function(app){
          return app.name === vm.buffer.toLowerCase().replace(/[^a-z0-9]/gi,'');
        });

        // display shadow app while new app is being created
        if(vm.buffer){
          if(!newApp){
            applications.push({
              name: vm.buffer,
              status: 'PENDING'
            })
          } else {
            vm.buffer = '';
          }
        }
        
        vm.applications = applications;     
        return vm.applications;
      }

      function error(response) {
        ErrorService.handle(response);
        if(timer){
          $interval.cancel(timer);
        }
      }
    }

    // Delete the application
    function deleteApplication(application) {
      ApplicationService.remove(application.name)
        .then(function() {
          selectedAction(application)
        });
    }

    function toggleServer(application) {
      if (application.status === 'START') {
        stopApplication(application);
      } else if (application.status === 'STOP') {
        startApplication(application);
      }
    }

    // Start the application
    function startApplication(application) {
      ApplicationService.start(application.name)
        .then(function() {
          checkActions();
        });
    }

    // Stop the application
    function stopApplication(application) {
      ApplicationService.stop(application.name)
        .then(function() {
          checkActions();
        });
    }

    function selectedAction (application) {
      // console.log('selectedAction', application);
      var index = vm.applicationsSelectedAction.map(function(application) { return application.name; }).indexOf(application.name);
      // var index = vm.applicationsSelectedAction.indexOf(applicationName);
      if( index === -1) {
        vm.applicationsSelectedAction.push(application);
      } else {
        vm.applicationsSelectedAction.splice(index, 1);
      }
      checkActions();
      // console.log(vm.applicationsSelectedAction);
    }

    function deleteActions(applications) {
      applications.map(function(application) {
        deleteApplication(application);
      });
      vm.applicationsSelectedAction = [];
    }

    function stopActions(applications) {
      applications.map(function(application) {
        stopApplication(application);
      });
      vm.applicationsSelectedAction = [];
    }

    function startActions(applications) {
      applications.map(function(application) {
        startApplication(application);
      });
      vm.applicationsSelectedAction = [];
    }

    function checkActions() {
      vm.actions = {
        stop: true,
        start: true
      }
      vm.applicationsSelectedAction.map(function(application) {
        console.log('checkActions');
        switch (application.status) {
          case 'START': 
            vm.actions.start = false;
            break;
          case 'STOP': 
            vm.actions.stop = false;
            break;
          default:
            break;
        }  
      });
      
    }

  }
})();
