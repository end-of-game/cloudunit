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
      deleteFile: deleteFile,
      addDirectory: addDirectory,
      unzipFile: unzipFile,
      zipFile: zipFile,
      editFile: editFile,
      getFile: getFile
    };



    ////////////////////////////////////////////////////////////////////

    // Liste l'arborescence de fichier d'un container
    function buildTree ( containerId, path ) {
      var dir = $resource ( '/file/container/:containerId',
        {
          containerId: containerId,
          path: path
        }
      );
      return dir.query().$promise;
    }

   function unzipFile ( containerId, applicationName, path, item) {
      var file = $resource ( 'file/unzip/container/:containerId/application/:applicationName',
      {
        containerId: containerId,
        applicationName: applicationName,
        path: path,
        fileName: item
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

      function zipFile ( containerId, applicationName, path, item) {
          var file = $resource ( 'file/zip/container/:containerId/application/:applicationName',
              {
                  containerId: containerId,
                  applicationName: applicationName,
                  path: path,
                  fileName: item
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
      var file = $resource ( '/file/content/container/:containerId/application/:applicationName', {
        containerId: containerId,
        applicationName: applicationName,
        fileName: fileName,
        path: path
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
      var file = $resource ( 'file/content/container/:containerId/application/:applicationName',
      {
        containerId: containerId,
        applicationName: applicationName
      },{ update: {method:'PUT'}
    });

    return file.update (
      {
        containerId: containerId,
        applicationName: applicationName
      }, {
        filePath: path,
        fileName: fileName,
        fileContent: fileContent}).$promise;
    }

    function deleteFile ( containerId, applicationName, path ) {
      var file = $resource ( '/file/container/:containerId/application/:applicationName',
        {
          containerId: containerId,
          applicationName: applicationName,
          path: path
        }
      );

      return file.delete().$promise;
    }

    function addDirectory ( containerId, applicationName, path ) {
      var request = $resource ('/file/container/:containerId/application/:applicationName',
        {
          containerId: containerId,
          applicationName: applicationName,
          path: path
        }
      );

      return request.save().$promise;
    }

  }
}) ();
