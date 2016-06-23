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

    vm.containers = [];
    vm.myContainer = {};
    vm.isLoading = true;
    vm.getContainers = getContainers;
    vm.stats = {};
    vm.cpuUsage = [];
    vm.monitoringService = MonitoringService;
    vm.data = [];
   
    
    
    
    
    
     vm.options = {
            chart: {
                type: 'stackedAreaChart',
                height: 450,
                margin : {
                    top: 20,
                    right: 20,
                    bottom: 30,
                    left: 40
                },
                x: function(d){return d.z;},
                y: function(d){return d.y;},
                "xAxis": {
                  "showMaxMin": false,
                  tickFormat: function(d) {
                       
                        return d3.time.format('%X')(new Date(d))
                    }
                },
                "yAxis": {
                  "showMaxMin": false
                },
                duration: 500,
                showControls: false,
  
            }
        };    
    
    
    vm.xAxisTickFormatFunction = function(){
        return function(d){
          return d3.time.format('%X')(new Date(d));

        }
      }

    $scope.$on ( '$destroy', function () {
      vm.monitoringService.stopPollStats();
    } );
    
    var test = 0;
    
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
           setTimeout(function() {
              vm.data[0] = {};
              vm.data[0].values = [];
              vm.data[0].key = "Cumulative Return";
              
              angular.forEach(vm.stats.cpuTotalUsage.labels, function(value, key) {
                vm.data[0].values[key] = {};
                vm.data[0].values[key].x = test;//vm.stats.cpuTotalUsage.labels[key];
                vm.data[0].values[key].y = vm.stats.cpuTotalUsage.series[0][key];
                
                
                var datetext = vm.stats.cpuTotalUsage.labels[key].split(':');
                console.log(datetext);
                var d = new Date(88,09,12,datetext[0],datetext[1],datetext[2]);
                /*d.setHours(datetext[0]);
                d.setMinutes(datetext[1]);
                d.setSeconds(datetext[2]);*/
                vm.data[0].values[key].z = d.getTime();
                vm.data[0].values[key].series = 0;
                test++;
              });
               console.log("UPDATE");
               console.log(vm.data);
               
            }, 1000);
           
            
            vm.isLoading = false;
            
          } )
        } )
        .catch ( function onGetContainersError ( reason ) {
          console.error ( reason ); //debug
        } );
    }

  }
}) ();

