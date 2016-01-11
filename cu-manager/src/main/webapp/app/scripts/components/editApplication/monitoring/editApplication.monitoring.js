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
    .directive ( 'editAppMonitoring', Monitoring );

  function Monitoring () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/editApplication/monitoring/editApplication.monitoring.html',
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
      $interval.cancel ( timer );
    } );

    getContainers ();

    function getContainers ( selectedContainer ) {
      return ApplicationService.listContainers ( $stateParams.name )
        .then ( function onGetContainersComplete ( containers ) {
          vm.containers = containers;
          vm.myContainer = selectedContainer || containers[0];
          vm.isLoading = false;

          MonitoringService.getMachineInfo ().then ( function (machineInfo) {
            updateStats ( vm.myContainer.name, machineInfo );
            if ( !timer ) {
              timer = pollStats ( vm.myContainer.name, machineInfo );
            }
          } ).catch ( function () {
            if ( timer ) {
              $interval.cancel ( timer );
            }
          } );
        } )
        .catch ( function onGetContainersError ( reason ) {
          console.error ( reason ); //debug
        } );
    }

    function updateStats ( containerName, machineInfo ) {
      MonitoringService.getStats ( containerName ).then ( function ( stats ) {
        vm.cpuTotalUsage = cpuTotalUsage ( stats );
        vm.cpuUsageBreakdown = cpuUsageBreakdown(stats);
        vm.memoryUsage = memoryUsage(stats);
        vm.networkUsage = networkUsage(stats);
        vm.networkErrors = networkErrors(stats);
        vm.cpuLoad = cpuLoad(stats, machineInfo);
      } );
    }

    var oneMegabyte = 1024 * 1024;

    function getInterval ( current, previous ) {
      var cur = new Date ( current );
      var prev = new Date ( previous );

      // ms -> ns.
      return (cur.getTime () - prev.getTime ()) * 1000000;
    }

    // Checks if the specified stats include the specified resource.
    function hasResource ( stats, resource ) {
      return stats.stats.length > 0 && stats.stats[0][resource];
    }


    function cpuTotalUsage ( stats ) {
      if ( stats.spec.has_cpu && !hasResource ( stats, "cpu" ) ) {
        return;
      }
      var data = {
        labels: [],
        series: [[]]
      };

      for ( var i = 1; i < stats.stats.length; i++ ) {
        var cur = stats.stats[i];
        var prev = stats.stats[i - 1];
        var intervalInNs = getInterval ( cur.timestamp, prev.timestamp );

        data.labels.push ( moment ( cur.timestamp ).format ( 'HH:MM:ss' ) );
        data.series[0].push ( (cur.cpu.usage.total - prev.cpu.usage.total) / intervalInNs );
      }
      return data;
    }

    function cpuUsageBreakdown ( stats ) {
      if ( stats.spec.has_cpu && !hasResource ( stats, "cpu" ) ) {
        return;
      }

      var data = {
        labels: [],
        series: [[], []]
      };

      for ( var i = 1; i < stats.stats.length; i++ ) {
        var cur = stats.stats[i];
        var prev = stats.stats[i - 1];
        var intervalInNs = getInterval ( cur.timestamp, prev.timestamp );

        data.labels.push ( moment ( cur.timestamp ).format ( 'HH:MM:ss' ) );
        data.series[0].push ( (cur.cpu.usage.user - prev.cpu.usage.user) / intervalInNs );
        data.series[1].push ( (cur.cpu.usage.system - prev.cpu.usage.system) / intervalInNs );
      }
      return data;

    }

    function cpuLoad ( stats, machineInfo ) {
      var data = {
        labels: [],
        series: [[]]
      };

      for ( var i = 1; i < stats.stats.length; i++ ) {
        var cur = stats.stats[i];
        var prev = stats.stats[i - 1];
        var cpuUsage = 0;

        var rawUsage = cur.cpu.usage.total - prev.cpu.usage.total;
        var intervalInNs = getInterval ( cur.timestamp, prev.timestamp );
        cpuUsage = Math.round ( ((rawUsage / intervalInNs) / machineInfo.num_cores) * 100 );
        if ( cpuUsage > 100 ) {
          cpuUsage = 100;
        }
        data.labels.push ( moment ( cur.timestamp ).format ( 'HH:MM:ss' ) );
        data.series[0].push ( cpuUsage );
      }

      return data;
    }

    function memoryUsage ( stats ) {
      if ( stats.spec.has_memory && !hasResource ( stats, "memory" ) ) {
        return;
      }

      var data = {
        labels: [],
        series: [[], []]
      };

      for ( var i = 1; i < stats.stats.length; i++ ) {
        var cur = stats.stats[i];
        data.labels.push ( moment ( cur.timestamp ).format ( 'HH:MM:ss' ) );
        data.series[0].push ( cur.memory.usage / oneMegabyte );
        data.series[1].push ( cur.memory.working_set / oneMegabyte );
      }
      return data;
    }

    function networkUsage ( stats ) {
      if ( stats.spec.has_network && !hasResource ( stats, "network" ) ) {
        return;
      }

      var data = {
        labels: [],
        series: [[], []]
      };

      for ( var i = 1; i < stats.stats.length; i++ ) {
        var cur = stats.stats[i];
        var prev = stats.stats[i - 1];
        var intervalInSec = getInterval ( cur.timestamp, prev.timestamp ) / 1000000000;
        data.labels.push ( moment ( cur.timestamp ).format ( 'HH:MM:ss' ) );
        data.series[0].push ( (cur.network.tx_bytes - prev.network.tx_bytes) / intervalInSec );
        data.series[1].push ( (cur.network.rx_bytes - prev.network.rx_bytes) / intervalInSec );
      }
      return data;
    }

    function networkErrors ( stats ) {
      if ( stats.spec.has_network && !hasResource ( stats, "network" ) ) {
        return;
      }

      var data = {
        labels: [],
        series: [[], []]
      };

      for ( var i = 1; i < stats.stats.length; i++ ) {
        var cur = stats.stats[i];
        var prev = stats.stats[i - 1];
        var intervalInSec = getInterval ( cur.timestamp, prev.timestamp ) / 1000000000;
        data.labels.push ( moment ( cur.timestamp ).format ( 'HH:MM:ss' ) );
        data.series[0].push ( (cur.network.tx_errors - prev.network.tx_errors) / intervalInSec );
        data.series[1].push ( (cur.network.rx_errors - prev.network.rx_errors) / intervalInSec );
      }
      return data;

    }


    function pollStats ( containerName, machineInfo ) {
      return $interval ( function () {
        updateStats ( containerName, machineInfo );
      }, 2000 )
    }
  }
}) ();


