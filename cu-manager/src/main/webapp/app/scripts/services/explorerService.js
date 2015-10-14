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
  angular.module ( 'webuiApp' )
    .factory ( 'ExplorerService', ExplorerService );

  ExplorerService.$inject = [
    '$resource'
  ];


  function ExplorerService ( $resource ) {

    return {
      buildTree: buildTree,
      downloadFile: downloadFile,
      deleteFile: deleteFile
    };


    ////////////////////////////////////////////////////////////////////

    // Liste l'arborescence de fichier d'un container
    function buildTree ( containerId, path ) {
      var dir = $resource ( '/file/container/:containerId/path/:path' );
      return dir.query ( {
        containerId: containerId,
        path: path
      } ).$promise;
    }

    function deleteFile ( containerId, applicationName, path ) {
      var file = $resource ( '/file/container/:containerId/application/:applicationName/path/:path' );

      return file.delete ( {
        containerId: containerId,
        applicationName: applicationName,
        path: path
      } ).$promise;
    }

    function downloadFile ( containerId, applicationName, path, fileName ) {
      var file = $resource ( '/file/container/:containerId/application/:applicationName/path/:path/fileName/:fileName', {
        containerId: containerId,
        applicationName: applicationName,
        path: path,
        fileName: fileName
      }, {
        get: {
          method: 'GET',
          transformResponse: function ( data, headers ) {
            var response = {};
            response.data = data;
            response.headers = headers ();
            return response;
          }
        }
      } );

      return file.get ().$promise;

    }
  }
}) ();






