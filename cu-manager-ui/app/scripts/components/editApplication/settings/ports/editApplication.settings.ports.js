/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */


(function () {
  'use strict';
  angular
    .module('webuiApp.editApplication')
    .component('portsComponent', PortsComponent());

  function PortsComponent(){
    return {
      templateUrl: 'scripts/components/editApplication/settings/ports/editApplication.settings.ports.html',
      bindings: {
        application: '=app'
      },
      controller: ['$scope', 'ApplicationService', PortsCtrl],
      controllerAs: 'ports',
    }
  }

  function PortsCtrl($scope, ApplicationService) {

    var vm = this;

    vm.number = '';
    vm.errorMsg = '';
    vm.createPort = createPort;
    vm.removePort = removePort;
    vm.urls = [];

    vm.natures = [
      {value: 'web'},
      {value: 'other'}
    ];

    vm.myNature = vm.natures[0];

    function createPort(applicationName, number, nature) {
      ApplicationService.createPort(applicationName, number, nature)
        .then(success)
        .catch(error);

      function success(response) {
        vm.errorMsg = '';
        vm.number = '';
        vm.nature = '';
      }

      function error(response) {
        vm.errorMsg = response.data.message;
        return vm.errorMsg;
      }
    }

    function removePort(applicationName, number) {
      ApplicationService.removePort(applicationName, number);
    }

  }
})();
