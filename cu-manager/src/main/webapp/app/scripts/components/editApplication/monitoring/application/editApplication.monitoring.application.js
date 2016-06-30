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
    .directive ( 'editAppMonitoringApplication', MonitoringApp );

  function MonitoringApp () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/editApplication/monitoring/application/editApplication.monitoring.application.html',
      scope: {
        app: '='
      },
      controller: [
        '$scope',
        'ApplicationService',
        '$stateParams',
        'MonitoringService',
        '$interval',
        MonitoringAppCtrl
      ],
      controllerAs: 'monitoringApp',
      bindToController: true
    };
  }

  function MonitoringAppCtrl ( $scope, ApplicationService, $stateParams, MonitoringService, $interval ) {

    // ------------------------------------------------------------------------
    // MONITORING
    // ------------------------------------------------------------------------

    var vm = this, timer;

    vm.queueName = '';
    vm.chooseQueue = chooseQueue;
    vm.queueStats = {};
    vm.selectedQueueStats = {};
    vm.loadStats = loadStats;
    vm.updateGraphs = updateGraphs;
    vm.displayGraph = [];
    
    function chooseQueue(queueName) {
      console.log(queueName);
      MonitoringService.chooseQueue(queueName)
        .then(success)
        .catch(error);

      function success(response) {
        console.log(response.value);
        vm.queueStats = response.value;
        vm.errorMsg = '';
        vm.queueName = '';
        $interval ( function () {
           MonitoringService.chooseQueue(queueName)
          .then(function(response) {
            angular.forEach(vm.displayGraph, function(value, key) {
              vm.displayGraph[key].data.push(
                {
                  "date":new Date(response.timestamp*1000),
                  "value":response.value[value.title]
                }
              );
            });
            console.log(vm.displayGraph);
          })
        }, 1000 )
        vm.queueStatsPoll = MonitoringService.queueStats;
        console.log(vm.queueStatsPoll);
      }

      function error(response) {
        vm.errorMsg = response.data.message;
        return vm.errorMsg;
      }
    }
    
    function loadStats(queueStatsName) {
      vm.displayGraph = [];
      angular.forEach(queueStatsName, function(value, key) {
        vm.displayGraph.push({
          data: [],
          title: key,
          description: '',
          x_accessor:'date',
          y_accessor:'value',
        });
      });
    }
   
   
   function updateGraphs(queueStatsName) {
     if(vm.selectedQueueStats[queueStatsName]) {
       console.log("delete graph");
     } else {
       loadStats()
     }
   }
   


  }
}) ();

