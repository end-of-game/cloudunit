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
    .directive ( 'editAppModules', Modules );

  function Modules () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/editApplication/images/editApplication.images.html',
      scope: {
        app: '='
      },
      controller: [
        'ImageService',
        'ModuleService',
        ModulesCtrl
      ],
      controllerAs: 'modules',
      bindToController: true
    };
  }

  function ModulesCtrl ( ImageService, ModuleService) {
    var vm = this;
    vm.moduleImages = [];
    vm.addModule = addModule;

    getModulesImages ();

    function getModulesImages () {
      return ImageService.findEnabledModule ()
        .then ( success )
        .catch ( error );

      function success ( images ) {
        vm.moduleImages = images;
        vm.moduleImages.push({
          id: 9999,
          name: "influxdb",
          path: "cloudunit/influxdb",
          displayName: "Influxdb",
          status: null,
          isTemp: true
        });
        
        vm.moduleImages.push({
          id: 9998,
          name: "mongo-3-2",
          path: "cloudunit/mongo-3-2",
          displayName: "Mongo 3.2",
          status: null,
          isTemp: true
        });
        
        return vm.moduleImages;
      }

      function error () {
        console.log ( 'cannot get modules images' );
      }
    }

    // Ajout d'un module
    function addModule ( applicationName, imageName ) {
      return ModuleService.addModule ( applicationName, imageName );
    }
  }
}) ();
