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
    .factory ( 'ExplorerService', ExplorerService );

  ExplorerService.$inject = [
    '$resource',
    '$http'
  ];


  function ExplorerService ( $resource, $http ) {

    return {
      buildTree: buildTree,
      //downloadFile: downloadFile,
      deleteFile: deleteFile,
      addDirectory: addDirectory,
      unzipFile: unzipFile,
      editFile: editFile,
      getFile: getFile
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
       
   function unzipFile ( containerId, applicationName, path, item) {
      var file = $resource ( 'file/unzip/container/:containerId/application/:applicationName/path/:path/fileName/:item',
      {
        containerId: containerId,
        applicationName: applicationName,
        path: path, 
        item: item
      },
      { 'update': { method: 'PUT',
          transformResponse: function ( data, headers ) {
            var response = {};
            response.data = data;
            response.headers = headers ();
            return response;
          } }
      });

      return file.update ().$promise;
    }
     
    function getFile ( containerId, applicationName, path, fileName ) {     
      var file = $resource ( '/file/content/container/:containerId/application/:applicationName/path/:path/fileName/:fileName', {
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
     
    function editFile ( containerId, applicationName, path, fileName, fileContent ) { 
      console.log(fileContent);
      var data, headers;
            headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json, text/plain, */*'
      };
      data = "fileContent=" + fileContent;
      return $http.post('file/content/container/' + containerId + '/application/' + applicationName + '/path/'+path + '/fileName/' + fileName, data, {
        headers: headers
      });
      
      /*var file = $resource ( 'file/content/container/:containerId/application/:applicationName/path/:path/fileName/:fileName',
      {
        containerId: containerId,
        applicationName: applicationName,
        path: path,
        fileName: fileName
      },{ test: {method:'POST', params: { fileContent: fileContent}}});

      return file.test ( ).$promise;*/
    }
       
    function deleteFile ( containerId, applicationName, path ) {
      var file = $resource ( '/file/container/:containerId/application/:applicationName/path/:path' );

      return file.delete ( {
        containerId: containerId,
        applicationName: applicationName,
        path: path
      } ).$promise;
    }

    function addDirectory ( containerId, applicationName, path ) {
      var request = $resource ( '/file/container/:containerId/application/:applicationName/path/:path', {
        containerId: containerId,
        applicationName: applicationName,
        path: path
      } );
      
      return request.save ().$promise;
    }
    
    /*function downloadFile ( containerId, applicationName, path, fileName ) {
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

    }*/
  }
}) ();






