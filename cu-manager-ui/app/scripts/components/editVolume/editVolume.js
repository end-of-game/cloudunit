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
        'VolumeService',
        'ErrorService',
        '$resource',
        EditVolumeCtrl
      ],
      controllerAs: 'editVolume',
    };
  }

  function EditVolumeCtrl (FeedService, ApplicationService, VolumeService, ErrorService, $resource) {

    var vm = this;
    vm.errorVolumeCreate = "";
    vm.errorVolumeDelete = "";

    vm.addVolume = addVolume;
    vm.deleteVolume = deleteVolume;

    vm.$onInit = function() {
      getListVolumes();
    }

    ////////////////////////////////////////////////////

    function addVolume ( volumeName ) {
      VolumeService.addVolume ( volumeName )
        .then ( function(response) {
          vm.newVolumeName = "";
          vm.errorVolumeCreate = '';
          vm.errorVolumeDelete = "";
          getListVolumes();
        })
        .catch(function(response) {
          vm.errorVolumeDelete = "";
          vm.errorVolumeCreate = response.data.message;
          getListVolumes();
        });  
    }

    function deleteVolume (id) {
      VolumeService.deleteVolume ( id )
        .then ( function(response) {
          getListVolumes();
        })
        .catch(function(response) {
          vm.errorVolumeCreate = "";
          vm.errorVolumeDelete = response.data.message;
          getListVolumes();
        }); 
    }

    function getListVolumes() {
      VolumeService.getListVolume ( )
        .then(function(response) {
          vm.volumes = response;
          angular.forEach(vm.volumes, function(volume, volumeIndex) {
            
            VolumeService.getLinkVolumeAssociation ( volume.id )
              .then(function(response) {
                vm.volumes[volumeIndex].applications = [];
                angular.forEach(response, function(application, applicationIndex) { 
                  ApplicationService.findByName( application.application ).then(function(response) {
                      vm.volumes[volumeIndex].applications.push(response);
                  });
                });
            });
          });
      });      
    }
  }
}) ();
