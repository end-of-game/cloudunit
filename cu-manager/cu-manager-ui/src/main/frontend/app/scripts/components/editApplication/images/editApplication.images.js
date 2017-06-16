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
    .module ( 'webuiApp.editApplication' )
    .component ( 'editAppModules', Modules() );

  function Modules () {
    return {
      templateUrl: 'scripts/components/editApplication/images/editApplication.images.html',
      bindings: {
        app: '='
      },
      controller: [
        '$rootScope',
        'ImageService',
        'ModuleService',
        '$stateParams',
        '$state',
        'ErrorService',
        ModulesCtrl
      ],
      controllerAs: 'modules',
    };
  }

  function ModulesCtrl ( $rootScope, ImageService, ModuleService, $stateParams, $state, ErrorService) {
    var vm = this;
    vm.serviceImages = [];
    vm.categorieImage = [];
    
    vm.addService = addService;
    vm.typeImage = $stateParams.typeImage;
    vm.serviceImage = $stateParams.serviceName;
    vm.errorAdding;
    
    vm.$onInit = function() {      
      getModulesImages ();
    }

    function getModulesImages () {
      ImageService.list ()
        .then ( function ( images ) {
          vm.serviceImages = images;

            angular.forEach(vm.serviceImages, function(image) {
              if (!(vm.categorieImage.map(function(categorieImage) { return categorieImage.serviceName; }).indexOf(image.serviceName) != -1 || image.serviceName == undefined)) {
                vm.categorieImage.push({serviceName: image.serviceName, type: image.type});
              }
            });
        });
    }

    function addService ( imageName ) {
      ModuleService.addService ( vm.app.name, imageName )
        .then(function (data) {
          vm.errorAdding = null;
          console.log('OK', data);
          return data;
        } ).catch(function(fallback) {
          console.error('ERRss', fallback)
          vm.errorAdding = fallback.data.message;
        });
    }
  }
}) ();
