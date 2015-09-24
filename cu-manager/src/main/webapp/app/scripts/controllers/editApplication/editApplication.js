/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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
   * @name webuiApp.controller:EditApplicationCtrl
   * @description
   * # EditApplicationCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp.editApplication')
    .controller('EditApplicationCtrl', EditApplicationCtrl);

  EditApplicationCtrl.$inject = [
    '$scope',
    '$interval',
    'ApplicationService',
    'CurrentApplication',
    'CurrentApplicationName',
    '$stateParams',
    'ErrorService'
  ];

  function EditApplicationCtrl($scope, $interval, ApplicationService, CurrentApplication, CurrentApplicationName, $stateParams, ErrorService) {

    // ------------------------------------------------------------------------
    // DEFINITION DU SCOPE
    // ------------------------------------------------------------------------

    var timer, vm = this;

    vm.application = CurrentApplication;
    vm.currentTab = $stateParams.tab;

    // Methode principale chargée de recharger le détail de l'application
    update();

    // polling sur la la méthode refresh
    timer = $interval(function () {
      update();
    }, 2000);


    // Pour des raisons de performance, arrête le polling
    // lorsque le scope est détruit
    $scope.$on('$destroy', function () {
      $interval.cancel(timer);
    });

    /////////////////////////////////////////////////////

    function update() {
      ApplicationService.findByName(CurrentApplicationName)
        .then(success)
        .catch(error);

      function success(application) {
        vm.application = application;
        return vm.application;
      }

      function error(response) {
        ErrorService.handle(response);
      }
    }

  }
})();

