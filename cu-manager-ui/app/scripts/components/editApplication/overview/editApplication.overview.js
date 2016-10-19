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

(function(){
  "use strict";
  angular.module('webuiApp.editApplication')
    .component('editAppOverview', Overview());

  function Overview () {
    return {
      templateUrl: 'scripts/components/editApplication/overview/editApplication.overview.html',
      bindings: {
        app: '='
      },
      controller: [
        '$scope',
        'ApplicationService',
        'ModuleService',
        '$filter',
        '$stateParams',
        OverviewCtrl
      ],
      controllerAs: 'overview',
    };
  }

  function OverviewCtrl($scope, ApplicationService, ModuleService, $filter, $stateParams){

    var vm = this;

    vm.toggleServer = toggleServer;
    vm.openPort = openPort;
    vm.removeModule = removeModule;
    vm.listEnvModule = [];
    vm.colapseModuleId;
    vm.portList = [];
    $scope.colapseOverview = true;

    $scope.$on ( 'application:ready', function ( e, data ) {
      vm.app = data.app;
      if(vm.app.server.status === 'START') {
        initializeEnvVar();
      }
    });

    vm.$onInit = function() {
      if(vm.app) {
        initializeEnvVar();
      }
    }

    ///////////////////////////////////////////


    function initializeEnvVar() {
      ApplicationService.getVariableEnvironment(vm.app.name, vm.app.server.name).then(function (data) {
        vm.app.env = data;

        angular.forEach(vm.app.modules, function(value, key) {
            vm.portList[value.id] = value.ports;
            ApplicationService.getVariableEnvironment($stateParams.name, value.name)
            .then(function successCallback(response) {
              vm.listEnvModule[value.id] = response;
            });
        });
      });
    }

    function refreshEnvVar () {
      ApplicationService.getVariableEnvironment(vm.app.name, vm.app.server.name)
      .then ( function (data) {
        vm.app.env = data;
      });
    }

    function openPort(idModule, statePort, portInContainer) {
      vm.pendingModules = true;
      ApplicationService.openPort(idModule, statePort, portInContainer)
      .then ( function (data) {
          vm.pendingModules = false;
          setTimeout(function() {
            initializeEnvVar();  
          }, 1000);          
      }, function (response) {
          vm.pendingModules = false;
      });
    }

    function toggleServer(application) {
      if (application.status === 'START') {
        stopApplication(application.name)
      } else if (application.status === 'STOP') {
        startApplication(application.name);
      }
    }

    function startApplication(applicationName) {
      ApplicationService.start(applicationName)
        .then(function() {
          initializeEnvVar();
          $scope.$emit('workInProgress', {delay: 3000});
        });
    }

    function stopApplication(applicationName) {
      ApplicationService.stop(applicationName);
      $scope.$emit('workInProgress', {delay: 3000});
    }

    function removeModule ( applicationName, moduleName ) {
      return ModuleService.removeModule ( applicationName, moduleName )
        .then(function() {
          refreshEnvVar();
        });
    }
  }
})();
