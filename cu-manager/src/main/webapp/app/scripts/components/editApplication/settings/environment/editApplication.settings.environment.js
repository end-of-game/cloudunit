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
  angular
    .module('webuiApp.editApplication')
    .component('environmentComponent', EnvironmentComponent());

  function EnvironmentComponent(){
    return {
      templateUrl: 'scripts/components/editApplication/settings/environment/editApplication.settings.environment.html',
      bindings: {
        application: '<app'
      },
      controller: [
        '$stateParams',
        '$q',
        'ApplicationService',
        'ErrorService',
        EnvironmentCtrl
      ],
      controllerAs: 'environment',
    }
  }

  function EnvironmentCtrl($stateParams, $q, ApplicationService, ErrorService) {

    var vm = this;
    vm.env = [];
    vm.containers = [];
    vm.myContainer = {};
    vm.pageSize = 5;
    vm.currentPage = 1;
    vm.environmentVariableKey = '';
    vm.environmentVariableValue = '';
    vm.addNoticeMsg = '';
    vm.addErrorMsg = '';
    vm.manageNoticeMsg = '';
    vm.manageErrorMsg = '';

    vm.predicate = 'value';
    vm.reverse = false;
    vm.order = order;

    vm.editEnv = editEnv;
    vm.deleteEnv = deleteEnv;
    vm.addEnv = addEnv;

    vm.$onInit = function() {  
      getContainers()
      .then(function() {
        getListEnvironmentVariable();
      })
      .catch(function(response) {
         ErrorService.handle(response);
      });
    }
    
    ////////////////////////////////////////////////

    function getListEnvironmentVariable() {
      ApplicationService.getListSettingsEnvironmentVariable($stateParams.name, vm.myContainer.id)
        .then(function(response) {
          vm.env = response;
        })
        .catch(function(response) {
          ErrorService.handle(response);
        });
    }

    function getContainers ( selectedContainer ) {
      var deferred = $q.defer ();
      vm.isLoading = true;
      ApplicationService.listContainers ( $stateParams.name )
        .then ( function ( containers ) {
          vm.containers = containers;
          vm.myContainer = selectedContainer || containers[0];
          vm.isLoading = false;
          deferred.resolve ( containers );
        } )
        .catch ( function ( response ) {
          deferred.reject ( response );
        } );
        return deferred.promise;
    }

    function deleteEnv (environmentVariable) {
      ApplicationService.deleteEnvironmentVariable (  $stateParams.name, vm.myContainer.id, environmentVariable.id )
        .then ( function() {
          cleanMessage();
          vm.env.splice(vm.env.indexOf(environmentVariable), 1);
          vm.manageNoticeMsg = 'The variable has been removed !';
        } )
        .catch (errorManageEnvironment);
    }
    
    function editEnv (environmentVariableID, environmentVariableKey, environmentVariableValue) {
      ApplicationService.editEnvironmentVariable ( $stateParams.name, vm.myContainer.id, environmentVariableID, environmentVariableKey, environmentVariableValue )
        .then(function(env) {
          cleanMessage();
          var elementPos = vm.env.map(function(x) {return x.id; }).indexOf(environmentVariableID);         
          vm.env[elementPos] = env;
          vm.manageNoticeMsg = 'The variable has been edited !';
        })
        .catch (function(response) {
          getListEnvironmentVariable();
          errorManageEnvironment(response);
        });
    }

    function addEnv (environmentVariableKey, environmentVariableValue) {
      ApplicationService.addEnvironmentVariable (  $stateParams.name, vm.myContainer.id, environmentVariableKey, environmentVariableValue )
        .then ( function(env) {
          cleanMessage();
          vm.env.push(env);
          vm.environmentVariableKey = '';
          vm.environmentVariableValue = '';
          vm.addNoticeMsg = 'Variable successfully created !';
        } )
        .catch (errorAddEnvironment);
    }

    function errorScript (res) {
      if(res.data.message) {
        vm.manageNoticeMsg = res.data.message;
      } else {
        vm.manageNoticeMsg = 'An error has been encountered!';
      }
      vm.manageErrorMsg = '';
    }

    function errorAddEnvironment (res) {
      cleanMessage();
      if(res.data.message) {
        vm.addErrorMsg = res.data.message;
      } else {
        vm.addErrorMsg = 'An error has been encountered!';
      }
    }

    function errorManageEnvironment (res) {
      cleanMessage();
      if(res.data.message) {
        vm.manageErrorMsg = res.data.message;
      } else {
        vm.manageErrorMsg = 'An error has been encountered!';
      }
    }

    function cleanMessage() {
      vm.addErrorMsg = '';
      vm.addNoticeMsg = '';
      vm.manageErrorMsg = '';
      vm.manageNoticeMsg = '';
    }

    function order (predicate) {
      vm.reverse = (vm.predicate === predicate) ? !vm.reverse : false;
      vm.predicate = predicate;
    }
  }
})();
