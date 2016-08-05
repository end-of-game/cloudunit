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
    vm.env = [];
    vm.pageSize = 5;
    vm.currentPage = 1;
    
    vm.$onChanges = function (changesObj) {
      if(changesObj.application) {
        if(changesObj.application.previousValue === undefined) {
          console.log(changesObj.application.currentValue.name);

          ApplicationService.getSettingsVariableEnvironment(changesObj.application.currentValue.name)
            .then(success)
            .catch(error);

          function success(response) {
            vm.env = response;
          }

          function error(response) {
            vm.env = [
              {key: 'key1', value : 'value1'},
              {key: 'key2', value : 'value2'},
              {key: 'key3', value : 'value3'},
              {key: 'key4', value : 'value4'},
              {key: 'key5', value : 'value5'},
              {key: 'key6', value : 'value6'},
              {key: 'key7', value : 'value7'},
              {key: 'key8', value : 'value8'}
            ]
          }

        }
      }
    };
  }
})();
