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
        'ApplicationService',
        'ErrorService',
        EnvironmentCtrl
      ],
      controllerAs: 'environment',
    }
  }

  function EnvironmentCtrl(ApplicationService, ErrorService) {

    var vm = this;
    vm.currentApplication = '';
    vm.env = [];
    vm.pageSize = 5;
    vm.currentPage = 1;
    vm.environmentVariableKey = '';
    vm.environmentVariableValue = '';

    vm.predicate = 'value';
    vm.reverse = false;
    vm.order = order;

    vm.editEnv = editEnv;
    vm.deleteEnv = deleteEnv;
    vm.addEnv = addEnv;

    vm.$onChanges = function (changesObj) {
      if(changesObj.application) {
        if((changesObj.application.previousValue === undefined)
          || vm.application !== undefined
        ) {
         vm.currentApplication = changesObj.application.currentValue.name;
          ApplicationService.getListSettingsEnvironmentVariable(vm.currentApplication)
            .then(success)
            .catch(error);

          function success (response) {
            vm.env = response;
          }

          function error (response) {
            ErrorService.handle(response);
          }
        }
      }
    };

    function deleteEnv (environmentVariable) {
      ApplicationService.deleteEnvironmentVariable (  vm.currentApplication, environmentVariable.id )
        .then ( function() {
          vm.env.splice(vm.env.indexOf(environmentVariable), 1);
          vm.noticeMsg = 'The variable has been removed!'
          vm.errorMsg = '';
        } )
        .catch (errorScript);
    }
    
    function editEnv (environmentVariableID, environmentVariableKey, environmentVariableValue) {
      ApplicationService.editEnvironmentVariable ( vm.currentApplication, environmentVariableID, environmentVariableKey, environmentVariableValue )
        .then(function(env) {
          var elementPos = vm.env.map(function(x) {return x.id; }).indexOf(environmentVariableID);
          vm.env[elementPos] = env.data;
          vm.noticeMsg = 'The variable has been edited!'
          vm.errorMsg = '';
        })
        .catch (errorScript);
    }

    function addEnv (environmentVariableKey, environmentVariableValue) {
      ApplicationService.addEnvironmentVariable (  vm.currentApplication, environmentVariableKey, environmentVariableValue )
        .then ( function(env) {
          vm.env.push(env);
          vm.environmentVariableKey = '';
          vm.environmentVariableValue = '';
          vm.noticeMsg = 'Variable successfully created!';
          vm.errorMsg = '';
        } )
        .catch (errorScript);
    }

    function errorScript (res) {
      if(res.data.message) {
        vm.errorMsg = res.data.message;
      } else {
        vm.errorMsg = 'An error has been encountered!';
      }
      vm.noticeMsg = '';
    }

    function order (predicate) {
      vm.reverse = (vm.predicate === predicate) ? !vm.reverse : false;
      vm.predicate = predicate;
    }
  }
})();
