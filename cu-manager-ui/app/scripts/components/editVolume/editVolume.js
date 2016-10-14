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
   * @name webuiApp.controller:EditVolumeCtrl
   * @description
   * # EditVolumeCtrl
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
        'ApplicationService',
        'ErrorService',
        EditVolumeCtrl
      ],
      controllerAs: 'editVolume',
    };
  }

  function EditVolumeCtrl (FeedService, ApplicationService, ErrorService) {

    var vm = this;
    vm.errorVolumeCreate = "";

    vm.addVolume = addVolume;
    vm.deleteVolume = deleteVolume;

    vm.$onInit = function() {
      getListVolumes();
    }

    ////////////////////////////////////////////////////

    function addVolume ( volumeName ) {
      ApplicationService.addVolume ( volumeName )
        .then ( function(response) {
          vm.newVolumeName = "";
          vm.errorVolumeCreate = '';
          getListVolumes();
        })
        .catch(function(response) {
          vm.errorVolumeCreate = response.data.message;
        });  
    }

    function deleteVolume (id) {
      ApplicationService.deleteVolume ( id )
        .then ( function(response) {
          getListVolumes();
        }); 
    }

    function getListVolumes() {
      console.log('getListVolumes');
        var dir = $resource('volume');

        var volumesList = dir.query().$promise;
        volumesList.then(function(response) {
            editVolume.volumes = response;
            console.log(response);
            angular.forEach(editVolume.volumes, function(volume, index) {
              
              var dir = $resource('volume/' + volume.id + '/associations');

              dir.query().$promise.then(function(response) {
                  console.log(response.data);
                  editVolume.volumes[index].applicationName = response;

              });

            });
//       ApplicationService.getListVolume ( )
//         .then ( function(response) {
//           vm.volumes = response;
        });
    }
  }
}) ();
