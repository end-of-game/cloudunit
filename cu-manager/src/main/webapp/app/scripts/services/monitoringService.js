(function () {
  'use strict';

  angular
    .module('webuiApp')
    .factory('MonitoringService', MonitoringService);

  MonitoringService.$inject = [
    '$resource'
  ];

  function MonitoringService($resource) {

    return {
      gatherNbRows: gatherNbRows
    };


    ////////////////////////////////////////////////////

    function gatherNbRows(containerId) {
      var logs = $resource('monitoring/:containerId');
      return logs.query({containerId: containerId}).$promise;
    }
  }
})();


