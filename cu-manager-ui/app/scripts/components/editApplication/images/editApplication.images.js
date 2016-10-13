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
    vm.moduleImages = [];
    vm.categorieImage = [];
    vm.addModule = addModule;
    vm.typeImage = $stateParams.typeImage;
    vm.errorAdding;
    
    vm.$onInit = function() {
      getModulesImages ();
    }
    
    function getModulesImages () {
      ImageService.findEnabledModule ()
        .then ( function ( images ) {
          vm.moduleImages = images;

          if(vm.typeImage === '') {
            angular.forEach(vm.moduleImages, function(image) {
              if (!(vm.categorieImage.indexOf(image.prefixEnv) != -1 || image.prefixEnv == undefined)) {
                vm.categorieImage.push(image.prefixEnv);
              }
            });
          }
        })
        .catch ( function (response) {
        ErrorService.handle(response);
        });
    }

    function addModule ( applicationName, imageName ) {
      ModuleService.addModule ( applicationName, imageName ).then(function (data) {
        vm.errorAdding = null;
        $state.go('editApplication.overview');
        return data;
      } ).catch(function(fallback) {
        vm.errorAdding = fallback.data.message;
      });
    }
  }
}) ();
