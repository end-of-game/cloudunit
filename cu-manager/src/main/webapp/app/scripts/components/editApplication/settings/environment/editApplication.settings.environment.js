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
      controller: ['$scope', 'ApplicationService', EnvironmentCtrl],
      controllerAs: 'environment',
    }
  }

  function EnvironmentCtrl($scope, ApplicationService) {

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
        if(changesObj.application.previousValue === undefined) {
         vm.currentApplication = changesObj.application.currentValue.name;

          ApplicationService.getListSettingsEnvironmentVariable(vm.currentApplication)
            .then(success)
            .catch(error);

          function success(response) {
            console.log(response[0]);
            vm.env = response;
          }

          function error(response) {
            vm.env = [
              {id: 1, key: 'key1', value : 'value1'},
              {id: 2, key: 'key2', value : 'value2'},
              {id: 3, key: 'key3', value : 'value3'},
              {id: 4, key: 'key4', value : 'value4'},
              {id: 5, key: 'key5', value : 'value5'},
              {id: 6, key: 'key6', value : 'value6'},
              {id: 7, key: 'key7', value : 'value7'},
              {id: 8, key: 'key8', value : 'value8'}
            ];
            console.log(vm.env[0]);
          }

        }
      }
    };

    function verify (test, id) {
      console.log(test);
      var regexp = /^[a-zA-Z0-9-_]+$/;
      var check = "checkme";
      if (check.search(regexp) == -1)
          { alert('invalid'); }
      else
          { alert('valid'); }
    }

    function deleteEnv(environmentVariable) {
      console.log(environmentVariable);
      ApplicationService.deleteEnvironmentVariable (  vm.currentApplication, environmentVariable.id )
        .then ( function() {
          vm.env.splice(vm.env.indexOf(environmentVariable), 1);
          vm.noticeMsg = 'The variable has been removed!'
          vm.errorMsg = '';
        } )
        .catch ( errorScript );
    }
    
    function editEnv (environmentVariableID, environmentVariableKey, environmentVariableValue) {
      console.log(environmentVariableID  + '  ' + environmentVariableKey + '  ' + environmentVariableValue );
      ApplicationService.editEnvironmentVariable ( vm.currentApplication, environmentVariableID, environmentVariableKey, environmentVariableValue )
        .then(function(env) {
          vm.env.splice(vm.scripts.indexOf(env.data), 1);
          vm.env.push(env.data);
          vm.noticeMsg = 'The variable has been edited!'
          vm.errorMsg = '';
        })
        .catch ( errorScript );
    }

    function errorScript(res) {
      if(res.data.message) {
        vm.errorMsg = res.data.message;
      } else {
        vm.errorMsg = 'An error has been encountered!';
      }
      vm.noticeMsg = '';
    }

    function addEnv (environmentVariableKey, environmentVariableValue) {
      console.log(environmentVariableKey + '  ' + environmentVariableValue);
      ApplicationService.addEnvironmentVariable (  vm.currentApplication, environmentVariableKey, environmentVariableValue )
        .then ( function(env) {
          console.log(env);
          vm.env.push(env);
          vm.environmentVariableKey = '';
          vm.environmentVariableValue = '';
          vm.noticeMsg = 'Variable successfully created!';
          vm.errorMsg = '';
        } )
        .catch ( function(response) {
          console.log(response);
          vm.errorMsg = 'An error has been encountered!';
          vm.noticeMsg = '';
        } );
    }

    function order ( predicate ) {
      vm.reverse = (vm.predicate === predicate) ? !vm.reverse : false;
      vm.predicate = predicate;
    }
  }
})();
