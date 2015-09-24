/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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
    .controller('ConfigJVMCtrl', ConfigJVMCtrl);

  ConfigJVMCtrl.$inject = ['$scope', 'ServerService'];

  function ConfigJVMCtrl($scope, ServerService) {
    var currentApplication, configjvmvm = this;
    currentApplication = $scope.editApp.application;

    // Config JVM
    configjvmvm.jvmOptions = currentApplication.servers[0].jvmOptions;
    configjvmvm.jvmMemory = currentApplication.servers[0].jvmMemory;
    configjvmvm.jvmRelease = currentApplication.servers[0].jvmRelease;
    configjvmvm.selectedJvmMemory = configjvmvm.jvmMemory;
    configjvmvm.selectedJvmRelease = configjvmvm.jvmRelease;

    configjvmvm.jvmMemorySizes = [512, 1024, 2048, 3072];
    configjvmvm.jvmReleases = ['jdk1.7.0_55','jdk1.8.0_25'];
    configjvmvm.saveConfigurationJVM = saveConfigurationJVM;

    // Fonction de sauvegarde des parametres de la jvm
    function saveConfigurationJVM(applicationName, jvmMemory, jvmOptions, jvmRelease) {
      ServerService.saveConfigurationJVM(applicationName, jvmMemory, jvmOptions, jvmRelease);
      $scope.$emit('workInProgress', {delay: 10000});
    }

  }
})();

