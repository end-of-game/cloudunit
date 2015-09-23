(function () {
  'use strict';
  angular.module ( 'webuiApp' )
    .factory ( 'LogService', LogService );

  LogService.$inject = [
    '$resource'
  ];


  function LogService ( $resource ) {

    return {
      gatherNbRows: gatherNbRows,
      getSources: getSources
    };


    ////////////////////////////////////////////////////////////////////

    // Liste de toutes les images qui sont activés quelque soit leur type
    function gatherNbRows ( applicationName, containerId, source, nbRows ) {
      var logs = $resource ( 'logs/:applicationName/container/:containerId/source/:source/rows/:nbRows' );
      return logs.query ( {
        applicationName: applicationName,
        containerId: containerId,
        source: source,
        nbRows: nbRows
      } ).$promise;
    }

    function getSources ( applicationName, containerId ) {
      var sources = $resource ( 'logs/sources/:applicationName/container/:containerId' );
      return sources.query ( {
        applicationName: applicationName,
        containerId: containerId
      } ).$promise;
    }
  }
}) ();






