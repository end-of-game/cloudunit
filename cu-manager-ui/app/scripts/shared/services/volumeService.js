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

  /**
   * @ngdoc service
   * @name webuiApp.VolumeService
   * @description
   * # VolumeService
   * Factory in the webuiApp.
   */
  angular
    .module ( 'webuiApp' )
    .factory ( 'VolumeService', VolumeService );

    VolumeService.$inject = [
      '$resource'
    ];


  function VolumeService ( $resource ) {

    return {
      getListVolume: getListVolume,
      getLinkVolumeAssociation: getLinkVolumeAssociation,
      getLinkVolume: getLinkVolume,
      addVolume: addVolume,
      deleteVolume: deleteVolume,
      linkVolume: linkVolume,
      unLinkVolume: unLinkVolume
    };

    function getListVolume ( ) {
      var dir = $resource ( 'volume' );
      return dir.query ( { } ).$promise;      
    }

    function getLinkVolumeAssociation ( volumeId ) {
      var dir = $resource ( 'volume/:volumeId/associations' );
      return dir.query ( { volumeId: volumeId } ).$promise; 
    }

    function getLinkVolume( containerName ) {
        var dir = $resource('server/volume/containerName/:containerName');
        return dir.query({
            containerName: containerName
        }).$promise;      
    }

    function addVolume ( volumeName ) {
        var data = {
            name: volumeName
        };

        var dir = $resource ( 'volume' );
        return dir.save ( { }, data ).$promise;
    }

    function deleteVolume ( volumeID ) {
        var dir = $resource ( 'volume/:id' );
        return dir.delete ( {
            id: volumeID
        }, {} ).$promise; 
    }

    function linkVolume ( applicationName, containerName, path, mode, volumeName ) {
        var data = {
            applicationName: applicationName,
            containerName: containerName,
            path: path,
            mode: mode,
            volumeName: volumeName
        };

        var dir = $resource ( 'server/volume' , { },
        { 
            'update': { 
                method: 'PUT',
                transformResponse: function ( data, headers ) {
                    var response = {};
                    response = JSON.parse(data);
                    return response;
                }
            }
        }
        );
        return dir.update( { }, data ).$promise; 
    }

    function unLinkVolume ( containerName, volumeName ) {
        var dir = $resource ( 'server/volume/:volumeName/container/:containerName' );
        return dir.delete ( { 
            volumeName: volumeName,
            containerName: containerName
        }, {} ).$promise; 
    }
  }
}) ();
