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
   * @ngdoc function
   * @name webuiApp.controller:TimelineCtrl
   * @description
   * # TimelineCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp.editVolume')
    .component('editVolume', EditVolume());


  function EditVolume () {
    return {
      templateUrl: 'scripts/components/editVolume/editVolume.html',
      bindings: {
        context: '='
      },
      controller: [
        'FeedService',
        'ErrorService',
        '$resource',
        '$http',
        EditVolumeCtrl
      ],
      controllerAs: 'editVolume',
    };
  }

  function EditVolumeCtrl (FeedService, ErrorService, $resource, $http) {

    var editVolume = this;

    editVolume.errorVolumeCreate = "";

    editVolume.$onInit = function() {
      getListVolumes();
    }

    editVolume.addVolume = function() {
        if(!editVolume.newVolumeName) {
            return 0;
        }

        $http({
            method: 'POST',
            url: '/volume',
            data: {
                name: editVolume.newVolumeName
            }
        }).then(function successCallback(response) {
            editVolume.newVolumeName = "";
            editVolume.errorVolumeCreate = '';
            getListVolumes();
        }, function errorCallback(response) {
            editVolume.errorVolumeCreate = response.data.message;
            console.log(response);
        });  
    }

    editVolume.deleteVolume = function(id) {
       $http({
            method: 'DELETE',
            url: '/volume/'+id
        }).then(function successCallback(response) {
            getListVolumes();
        }, function errorCallback(response) {
               
        }); 
    }

    ////////////////////////////////////////////////////

    function getListVolumes() {
        var dir = $resource('volume');

        var volumesList = dir.query().$promise;
        volumesList.then(function(response) {
            editVolume.volumes = response;
        });
    }
  }
}) ();

