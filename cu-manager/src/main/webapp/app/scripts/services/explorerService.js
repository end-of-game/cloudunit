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






