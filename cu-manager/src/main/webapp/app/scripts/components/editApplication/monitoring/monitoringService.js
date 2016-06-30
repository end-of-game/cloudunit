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
    .module ( 'webuiApp' )
    .factory ( 'MonitoringService', MonitoringService );

  MonitoringService.$inject = [
    '$http',
    '$interval',
    '$q'
  ];

  function MonitoringService ( $http, $interval, $q ) {

    var oneMegabyte = 1024 * 1024;

    return {
      stats: {},
      initStats: initStats,
      stopPollStats: stopPollStats,
      chooseQueue: chooseQueue
    };


    ////////////////////////////////////////////////////

    function getMachineInfo () {
      // todo change endpoint
      return $http.get ( 'monitoring/api/machine' ).then ( function ( response ) {
        return angular.copy ( response.data );
      } );
    }


    function getStats () {
      return $http.get ( "monitoring/api/containers/docker/" + this.containerName ).then ( function ( response ) {
        return angular.copy ( response.data );
      } );
    }

    function initStats ( containerName ) {
      var self = this;
      self.containerName = containerName;
      return getMachineInfo.call(self).then ( function ( machineInfo ) {
        updateStats.call (self, machineInfo );
        if ( !self.timer ) {
          self.timer = pollStats.call ( self, machineInfo );
        }
      } ).catch ( function () {
        stopPollStats.call(self)
      } );
    }

    function updateStats ( machineInfo ) {
      var self = this;
      return getStats.call (self).then ( function ( stats ) {
        self.stats.cpuTotalUsage = getCpuTotalUsage( stats );
        self.stats.cpuUsageBreakdown = getCpuUsageBreakdown ( stats );
        self.stats.cpuPerCoreUsage  = getCpuPerCoreUsage( machineInfo, stats );
        self.stats.memoryUsage = getMemoryUsage( stats );
        self.stats.networkUsage = getNetworkUsage( stats );
        self.stats.networkErrors = getNetworkErrors ( stats );
        self.stats.cpuLoad = getCpuLoad( stats, machineInfo );
      } );
    }

    function pollStats ( machineInfo ) {
      var self = this;
      return $interval ( function () {
        updateStats.call (self, machineInfo );
      }, 2000 )
    }

    function stopPollStats(){
      if(this.timer){
        $interval.cancel(this.timer);
        this.timer = null;
      }
    }

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


    function getCpuTotalUsage ( stats ) {
      if ( stats.spec.has_cpu && !hasResource ( stats, "cpu" ) ) {
        return;
      }
      var data = {
        legends: ['Total'],
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

    function getCpuPerCoreUsage ( machineInfo, stats ) {
      if ( stats.spec.has_cpu && !hasResource ( stats, "cpu" ) ) {
        return;
      }

      var data = {
        legends: [],
        labels: [],
        series: []
      };

      for ( var i = 0; i < machineInfo.num_cores; i++ ) {
        data.legends.push ( "Core " + i );
        data.series[i] = [];
      }
      for ( var i = 1; i < stats.stats.length; i++ ) {
        var cur = stats.stats[i];
        var prev = stats.stats[i - 1];
        var intervalInNs = getInterval ( cur.timestamp, prev.timestamp );
        data.labels.push ( moment ( cur.timestamp ).format ( 'HH:MM:ss' ) );
        for ( var j = 0; j < machineInfo.num_cores; j++ ) {
          data.series[j].push ( (cur.cpu.usage.per_cpu_usage[j] - prev.cpu.usage.per_cpu_usage[j]) / intervalInNs );
        }
      }
      return data;
    }

    function getCpuUsageBreakdown ( stats ) {
      if ( stats.spec.has_cpu && !hasResource ( stats, "cpu" ) ) {
        return;
      }

      var data = {
        legends: ['User', 'Kernel'],
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

    function getCpuLoad ( stats, machineInfo ) {
      var data = {
        legends: ['Memory'],
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

    function getMemoryUsage ( stats ) {
      if ( stats.spec.has_memory && !hasResource ( stats, "memory" ) ) {
        return;
      }

      var data = {
        legends: ['Total', 'Hot'],
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

    function getNetworkUsage ( stats ) {
      if ( stats.spec.has_network && !hasResource ( stats, "network" ) ) {
        return;
      }

      var data = {
        legends: ['Tx', 'Rx'],
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

    function getNetworkErrors ( stats ) {
      if ( stats.spec.has_network && !hasResource ( stats, "network" ) ) {
        return;
      }

      var data = {
        legends: ['Tx', 'Rx'],
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
    
    function chooseQueue (queueName) {
      var deferred = $q.defer ();
      $http.get ( 'http://192.168.2.117:8081/jolokia/read/jboss.as:subsystem=messaging-activemq,server=default,jms-queue=' + queueName)
        .then ( function onSuccess ( response ) {
        if(response.data.status !== 200) {
          deferred.reject ({
            data: { 
              message : "This address is not found!"
            }
          });
        } else {
          deferred.resolve ( response.data );  
        }
      } )
      .catch ( function onError ( reason ) {
        deferred.reject ( reason );
      } );
      return deferred.promise;
    }


  }
}) ();


