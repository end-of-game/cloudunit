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
    configjvmvm.jvmReleases = ['7','8'];
    configjvmvm.saveConfigurationJVM = saveConfigurationJVM;

    // Fonction de sauvegarde des parametres de la jvm
    function saveConfigurationJVM(applicationName, jvmMemory, jvmOptions, jvmRelease) {
      ServerService.saveConfigurationJVM(applicationName, jvmMemory, jvmOptions, jvmRelease);
      $scope.$emit('workInProgress', {delay: 10000});
    }

  }
})();

