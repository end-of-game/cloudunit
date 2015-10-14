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
   * @name webuiApp.controller:MainCtrl
   * @description
   * # MainCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .controller('DashboardCtrl', DashboardCtrl);

  DashboardCtrl.$inject = [
    '$scope',
    '$interval',
    'ApplicationService',
    'ErrorService'
  ];


  function DashboardCtrl($scope, $interval, ApplicationService, ErrorService) {
    var timer, vm = this;
    vm.applications = [];
    vm.selectedItem = 'All';
    vm.search = '';
    vm.deleteApplication = deleteApplication;
    vm.toggleServer = toggleServer;

    update();

    // Polling on refresh
    timer = $interval(function () {
      update();
    }, 2000);


    $scope.$on('$destroy', function () {
      $interval.cancel(timer);
    });

    /////////////////////////////////////////////

    // Refresh the application list
    function update() {
      ApplicationService.list()
        .then(success)
        .catch(error);

      function success(applications) {
        vm.applications = applications;
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
    function deleteApplication(applicationName) {
      ApplicationService.remove(applicationName);
    }

    function toggleServer(application) {
      if (application.status === 'START') {
        stopApplication(application.name);
      } else if (application.status === 'STOP') {
        startApplication(application.name);
      }
    }

    // Start the application
    function startApplication(applicationName) {
      ApplicationService.start(applicationName);
    }

    // Stop the application
    function stopApplication(applicationName) {
      ApplicationService.stop(applicationName);
    }
  }
})();

