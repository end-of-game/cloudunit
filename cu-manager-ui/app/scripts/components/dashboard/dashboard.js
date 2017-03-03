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
    vm.stopApplication = stopApplication;
    vm.startApplication = startApplication;

    vm.toggleServer = toggleServer;
    vm.buffer = '';
    vm.selectedDisplayStyle = 'Grid';
    vm.applicationsSelectedAction = [];
    vm.selectForAction = selectForAction;
    vm.executeAction = executeAction;

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
        
        // checked if application selected for actions
        applications = applications.map(function(refreshApplication){
            if((vm.applicationsSelectedAction
                  .map(function(application) { return application.name; })
                  .indexOf(refreshApplication.name) !== -1)) {
                    refreshApplication.checked = true;
            } else {
              refreshApplication.checked = false;
            }
          return refreshApplication;
        });

        // check if application has been deleted concurrently for actions
        vm.applicationsSelectedAction = applications.filter(function(refreshApplication) {
          return (vm.applicationsSelectedAction.map(function(application) { return application.name; })
                    .indexOf(refreshApplication.name) !== -1)
        });

        vm.applications = applications;
        // update actions concurrently
        checkActions();
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
          vm.applicationsSelectedAction = vm.applicationsSelectedAction.filter(
            function(applicationAction) {
              return applicationAction.name !== application.name;
            });
            checkActions();
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

    // push/remove application in applicationsSelectedAction
    function selectForAction (application) {
      var index = vm.applicationsSelectedAction
        .map(function(application) { return application.name; })
        .indexOf(application.name);
      if( index === -1) {
        vm.applicationsSelectedAction.push(application);
      } else {
        vm.applicationsSelectedAction.splice(index, 1);
      }
      checkActions();
    }

    // execute action on selectedAction
    // if status authorize to execute action
    function executeAction(applications, actionFunction) {
        applications.map(function(application) {
          if((actionFunction === vm.stopApplication && application.status === 'START')
            || (actionFunction === vm.startApplication && application.status === 'STOP')
            || (actionFunction === vm.deleteApplication)) {
            actionFunction.call(this, application);
          }
        });
        vm.applicationsSelectedAction = [];
    }

    // check if one action could be apply an action
    // this action could be true
    function checkActions() {
      vm.actions = {
        stop: false,
        start: false
      }
      vm.applicationsSelectedAction.map(function(application) {
        switch (application.status) {
          case 'START': 
            vm.actions.stop = true;
            break;
          case 'STOP': 
            vm.actions.start = true;
            break;
          default:
            break;
        }  
      });
    }

  }
})();
