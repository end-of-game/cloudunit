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
    .directive('aliasComponent', AliasComponent);

  function AliasComponent(){
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/editApplication/settings/alias/editApplication.settings.alias.html',
      scope: {
        application: '=app'
      },
      controller: ['$scope', 'ApplicationService', AliasCtrl],
      controllerAs: 'alias',
      bindToController: true
    }
  }

  function AliasCtrl($scope, ApplicationService) {

    var vm = this;
    console.log(this.application);

    vm.domain = '';
    vm.errorMsg = '';
    vm.createAlias = createAlias;
    vm.removeAlias = removeAlias;

    function createAlias(applicationName, domain) {
      ApplicationService.createAlias(applicationName, domain)
        .then(success)
        .catch(error);

      function success() {
        vm.errorMsg = '';
        vm.domain = '';
      }


      function error(response) {
        vm.errorMsg = response.data.message;
        return vm.errorMsg;
      }
    }
    function removeAlias(applicationName, domain) {
      ApplicationService.removeAlias(applicationName, domain);
    }
  }
})();
