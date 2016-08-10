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
  angular
    .module('webuiApp.editApplication')
    .component('volumeComponent', volumeComponent());

  function volumeComponent(){
    return {
      templateUrl: 'scripts/components/editApplication/settings/volume/editApplication.settings.volume.html',
      bindings: {
        application: '<app'
      },
      controller: [
        'ApplicationService',
        'ErrorService',
        volumeCtrl
      ],
      controllerAs: 'volume',
    }
  }

  function volumeCtrl(ApplicationService, ErrorService) {

    var vm = this;
    vm.currentApplication = '';
    vm.volume = [];
    vm.pageSize = 5;
    vm.currentPage = 1;
    vm.volumeName = '';
    vm.volumePath = '';

    vm.predicate = 'name';
    vm.reverse = false;
    vm.order = order;

    vm.editvolume = editvolume;
    vm.deletevolume = deletevolume;
    vm.addvolume = addvolume;

    vm.$onChanges = function (changesObj) {
      if(changesObj.application) {
        if((changesObj.application.previousValue === undefined)
          || vm.application !== undefined
        ) {
         vm.currentApplication = changesObj.application.currentValue.name;
          ApplicationService.getListSettingsVolume(vm.currentApplication)
            .then(success)
            .catch(error);

          function success (response) {
            vm.volume = response;
          }

          function error (response) {
            ErrorService.handle(response);
          }
        }
      }
    };

    function deleteVolume (volume) {
      ApplicationService.deleteVolume (  vm.currentApplication, volume.id )
        .then ( function() {
          vm.volume.splice(vm.volume.indexOf(volume), 1);
          vm.noticeMsg = 'The volume has been removed!'
          vm.errorMsg = '';
        } )
        .catch (errorScript);
    }
    
    function editVolume (volumeID, volumeName, volumePath) {
      ApplicationService.editVolume ( vm.currentApplication, volumeID, volumeName, volumePath )
        .then(function(volume) {
          var elementPos = vm.volume.map(function(x) {return x.id; }).indexOf(volumeID);
          vm.volume[elementPos] = volume.data;
          vm.noticeMsg = 'The volume has been edited!'
          vm.errorMsg = '';
        })
        .catch (errorScript);
    }

    function addVolume (volumeName, volumePath) {
      ApplicationService.addVolume (  vm.currentApplication, volumeName, volumePath )
        .then ( function(volume) {
          vm.volume.push(volume);
          vm.volumeName = '';
          vm.volumePath = '';
          vm.noticeMsg = 'volume successfully created!';
          vm.errorMsg = '';
        } )
        .catch (errorScript);
    }

    function errorScript (res) {
      if(res.data.message) {
        vm.errorMsg = res.data.message;
      } else {
        vm.errorMsg = 'An error has been encountered!';
      }
      vm.noticeMsg = '';
    }

    function order (predicate) {
      vm.reverse = (vm.predicate === predicate) ? !vm.reverse : false;
      vm.predicate = predicate;
    }
  }
})();
