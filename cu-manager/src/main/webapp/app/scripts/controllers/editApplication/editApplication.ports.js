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
    .controller('PortsCtrl', PortsCtrl);

  PortsCtrl.$inject = ['$scope', 'ApplicationService'];

  function PortsCtrl($scope, ApplicationService) {

    var vm = this;
    vm.number = '';
    vm.errorMsg = '';
    vm.createPort = createPort;
    vm.removePort = removePort;
    vm.restartApplication = restartApplication;
    vm.urls = [];

    vm.natures = [
      {value: 'web'},
      {value: 'other'}
    ];
    vm.myNature = vm.natures[0];

    // Démarrage de l'application
    function restartApplication(applicationName) {
      ApplicationService.restart(applicationName);
      $scope.$emit('workInProgress', {delay: 3000});
    }


    function createPort(applicationName, number, nature) {
      ApplicationService.createPort(applicationName, number, nature)
        .then(success)
        .catch(error);

      function success() {
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
