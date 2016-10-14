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
        volumeCtrl
      ],
      controllerAs: 'volume',
    }
  }

  function volumeCtrl($stateParams, $q, ApplicationService, ErrorService) {

    var vm = this;
    vm.volumes = [];
    vm.containers = [];
    vm.myContainer = {};
    vm.isLoading = true;
    vm.listVolumes = '';
    vm.IReadOnly = 'rw';
    vm.pageSize = 5;
    vm.currentPage = 1;

    vm.addNoticeMsg = '';
    vm.addErrorMsg = '';
    vm.manageNoticeMsg = '';
    vm.manageErrorMsg = '';

    vm.predicate = 'name';
    vm.reverse = false;
    vm.order = order;
    
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

    function getListVolume() {
        ApplicationService.getListVolume ( )
        .then ( function(response) {
          vm.volumes = response;
        });
    }

    function setLinkVolume() {
        ApplicationService.linkVolume ( $stateParams.name, vm.myContainer.name, vm.createLinkPath, vm.IReadOnly, vm.volumePicked )
        .then ( function(response) {
            cleanMessage();
            vm.getLinkVolume();
            vm.createLinkPath = '';
            vm.volumePicked = '';
            vm.IReadOnly = 'rw';
            vm.addNoticeMsg = 'volume successfully linked !';
        }).catch (function(response) {
            cleanMessage();
            if(response.data.message) {
              vm.addErrorMsg = response.data.message;
            } else {
              vm.addErrorMsg = 'An error has been encountered !';
            };    
        });
    }

    function breakLink(volume) {
        ApplicationService.unLinkVolume ( vm.myContainer.name, volume.name )
        .then ( function(response) {
            vm.getLinkVolume();
            cleanMessage();
            vm.manageNoticeMsg = 'volume successfully unlinked !';
        }).catch (function(response) {
          cleanMessage();
          if(response.data.message) {
            vm.manageErrorMsg = response.data.message;
          } else {
            vm.manageErrorMsg = 'An error has been encountered !';
          };  
        });
    }

    function getLinkVolume() {
        ApplicationService.getLinkVolume ( vm.myContainer.name)
        .then ( function(response) {
            vm.listVolumes = response;
        });
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
