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
        '$stateParams',
        '$q',
        'ApplicationService',
        'ErrorService',
        '$resource',
        '$http',
        volumeCtrl
      ],
      controllerAs: 'volume',
    }
  }

  function volumeCtrl($stateParams, $q, ApplicationService, ErrorService, $resource, $http) {

    var vm = this;
    vm.volumes = [];
    vm.containers = [];
    vm.myContainer = {};
    vm.isLoading = true;
    vm.pageSize = 5;
    vm.currentPage = 1;
    vm.volumeName = '';
    vm.listVolumes = '';
    vm.volumePath = '';
    vm.addNoticeMsg = '';
    vm.addErrorMsg = '';
    vm.manageNoticeMsg = '';
    vm.manageErrorMsg = '';

    vm.predicate = 'name';
    vm.reverse = false;
    vm.order = order;
    
    vm.addVolume = addVolume;
    vm.editVolume = editVolume;
    vm.deleteVolume = deleteVolume;
    vm.setLinkVolume = setLinkVolume;
    vm.getLinkVolume = getLinkVolume;
    vm.breakLink = breakLink;

    vm.$onInit = function() { 
        getContainers().then(function() {
            getListVolume();
            getLinkVolume();
        })
        .catch(function(response) {
            ErrorService.handle(response);
        });
    }

    ////////////////////////////////////////////////

    function setLinkVolume() {
        var mode = 'rw';
        if (vm.IReadOnly === true) {
            mode = 'ro'
        }
        var data = {
            applicationName: $stateParams.name,
            containerName: vm.myContainer.name,
            path: vm.createLinkPath,
            mode: mode,
            volumeName: vm.volumePicked
        };
        var urlLink = 'server/volume/';

        $http({
            method: 'PUT',
            url: urlLink,
            data: data
        }).then(function successCallback(response) {
            console.log(response);
            vm.getLinkVolume();
        }, function errorCallback(response) {
            vm.errorLinkCreate = response.data.message;
        });
    }

    function getLinkVolume() {
        var urlLink = 'server/volume/containerName/' + vm.myContainer.name;

        $http({
            method: 'GET',
            url: urlLink
        }).then(function successCallback(response) {
            vm.listVolumes = response.data;
            console.log(response.data);
        }, function errorCallback(response) {
            console.log(response);
        });
    }

    function getListVolume() {
        var dir = $resource('volume');

        var volumesList = dir.query().$promise;
        volumesList.then(function(response) {
            vm.volumes = response;
        })
    }

    function breakLink(volume) {
        var data = {
            applicationName: $stateParams.name,
            containerName: vm.myContainer.name,
            path: volume.volumeAssociations[0].path,
            mode: 'rw',
            volumeName: volume.name
        };
        var urlLink = 'server/volume/';

        $http({
            method: 'DELETE',
            url: urlLink,
            data: data
        }).then(function successCallback(response) {
            console.log(response);
            vm.getLinkVolume();
        }, function errorCallback(response) {
            vm.errorLinkCreate = response.data.message;
        });

    }

    function getContainers (selectedContainer) {
      var deferred = $q.defer ();
      vm.isLoading = true;
      ApplicationService.listContainers ( $stateParams.name )
        .then ( function ( containers ) {
          vm.containers = containers;
          vm.myContainer = selectedContainer || containers[0];
          vm.isLoading = false;
          deferred.resolve ( containers );
        } )
        .catch ( function ( response ) {
          deferred.reject ( response );
        } );
        return deferred.promise;
    }

    function deleteVolume (volume) {
      ApplicationService.deleteVolume (  $stateParams.name, vm.myContainer.name, volume.id )
        .then ( function() {
          cleanMessage();
          getListVolume();
          vm.manageNoticeMsg = 'The volume has been removed!'
        } )
        .catch (errorManageVolume);
    }
    
    function editVolume (volumeID, volumeName, volumePath) {
      ApplicationService.editVolume ( $stateParams.name, vm.myContainer.name, volumeID, volumeName, volumePath )
        .then(function(volume) {
          cleanMessage();
          getListVolume();
          vm.manageNoticeMsg = 'The volume has been edited!'
        })
        .catch ( function(response) {
          getListVolume();
          errorManageVolume(response);
        } );
    }

    function addVolume (volumeName, volumePath) {
      ApplicationService.addVolume (  $stateParams.name, vm.myContainer.name, volumeName, volumePath )
        .then ( function(volume) {
          cleanMessage();
          getListVolume();
          vm.volumeName = '';
          vm.volumePath = '';
          vm.addNoticeMsg = 'volume successfully created !';
        } )
        .catch (errorAddVolume);
    }

    function errorAddVolume (res) {
      cleanMessage();
      if(res.data.message) {
        vm.addErrorMsg = res.data.message;
      } else {
        vm.addErrorMsg = 'An error has been encountered !';
      }
    }

    function errorManageVolume (res) {
      cleanMessage();
      if(res.data.message) {
        vm.manageErrorMsg = res.data.message;
      } else {
        vm.manageErrorMsg = 'An error has been encountered !';
      };
    }

    function cleanMessage() {
      vm.addErrorMsg = '';
      vm.addNoticeMsg = '';
      vm.manageErrorMsg = '';
      vm.manageNoticeMsg = '';
    }

    function order (predicate) {
      vm.reverse = (vm.predicate === predicate) ? !vm.reverse : false;
      vm.predicate = predicate;
    }
  }
})();
