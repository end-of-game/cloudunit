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
    .module ( 'webuiApp.editApplication' )
    .directive ( 'editAppMonitoringContainers', Monitoring );

  function Monitoring () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/editApplication/monitoring/containers/editApplication.monitoring.containers.html',
      scope: {
        app: '='
      },
      controller: [
        '$scope',
        'ApplicationService',
        '$stateParams',
        'MonitoringService',
        '$interval',
        MonitoringCtrl
      ],
      controllerAs: 'monitoring',
      bindToController: true
    };
  }

  function MonitoringCtrl ( $scope, ApplicationService, $stateParams, MonitoringService, $interval ) {

    // ------------------------------------------------------------------------
    // MONITORING
    // ------------------------------------------------------------------------

    var vm = this, timer;

    vm.containers = [];
    vm.myContainer = {};
    vm.isLoading = true;
    vm.getContainers = getContainers;
    vm.stats = {};
    vm.cpuUsage = [];
    vm.monitoringService = MonitoringService;

    vm.chartOptions = {
      scaleMinSpace: 300
    };

    vm.chartPieOptions = {
      donut: true,
      donutWidth: 20,
      startAngle: 270,
      total: 100
    };

    $scope.$on ( '$destroy', function () {
      vm.monitoringService.stopPollStats();
    } );

    getContainers ();

    function getContainers ( selectedContainer ) {
      vm.isLoading = true;
      return ApplicationService.listContainers ( $stateParams.name )
        .then ( function onGetContainersComplete ( containers ) {
          vm.containers = containers;
          vm.myContainer = selectedContainer || containers[0];

          console.log(vm.myContainer);
          vm.monitoringService.initStats ( vm.myContainer.name ).then ( function () {
            vm.stats = vm.monitoringService.stats;
            vm.isLoading = false;
          } )
        } )
        .catch ( function onGetContainersError ( reason ) {
          console.error ( reason ); //debug
        } );
    }

  }
}) ();
