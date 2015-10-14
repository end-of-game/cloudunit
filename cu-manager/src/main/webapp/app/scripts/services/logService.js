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






