// jscs:disable safeContextKeyword
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

(function() {
  'use strict';
  angular
    .module('webuiApp.editApplication')
    .component('jvmComponent', ConfigJVMComponent());

  function ConfigJVMComponent() {
    return {
      templateUrl: 'scripts/components/editApplication/settings/jvm/editApplication.settings.configureJVM.html',
      bindings: {
        application: '<app',
      },
      controller: ['$scope', 'JVMService', '$filter', ConfigJVMCtrl],
      controllerAs: 'configjvm',
    };
  }

  function ConfigJVMCtrl($scope, JVMService, $filter) {
    var vm = this;

    // Config JVM
    vm.jvmOptions = '';
    vm.jvmMemory = '';
    vm.jvmRelease = '';
    vm.selectedJvmMemory = '';
    vm.selectedJvmRelease = '';


    vm.jvmMemorySizes = [512, 1024, 2048, 3072, 4096];
    vm.jvmReleases = ['java7', 'java8', 'java9'];
    vm.saveConfigurationJVM = saveConfigurationJVM;


    vm.$onChanges = function (changesObj) {
      if(changesObj.application) {
        if((changesObj.application.previousValue === undefined)
          || vm.application !== undefined
        ) {
          vm.jvmOptions = $filter('cleanEscapeSlashAndReverse')(vm.application.server.jvmOptions, true);
          vm.jvmMemory = vm.application.server.jvmMemory;
          vm.jvmRelease = vm.application.server.jvmRelease;
          vm.selectedJvmMemory = vm.jvmMemory;
          vm.selectedJvmRelease = vm.jvmRelease;
        }
      }
    };

    // Function to save the JVM parameters
    function saveConfigurationJVM(applicationName, jvmMemory, jvmOptions, jvmRelease) {
      jvmOptions = $filter('cleanEscapeSlashAndReverse')(jvmOptions, false);
      JVMService.saveConfigurationJVM(applicationName, vm.application.server, jvmMemory, jvmOptions, jvmRelease);
      $scope.$emit('workInProgress', { delay: 10000 });
    }

  }
})();
