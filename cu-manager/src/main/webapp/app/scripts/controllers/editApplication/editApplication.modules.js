(function () {
  'use strict';
  angular
    .module('webuiApp.editApplication')
    .controller('ModulesCtrl', ModulesCtrl);

  ModulesCtrl.$inject = ['ModuleService', 'ImageService'];

  function ModulesCtrl(ModuleService, ImageService) {

    var modulesvm = this;
    modulesvm.moduleImages = [];
    modulesvm.addModule = addModule;
    modulesvm.removeModule = removeModule;

    getModulesImages();

    function getModulesImages() {
      return ImageService.findEnabledModule()
        .then(success)
        .catch(error);

      function success(images) {
        modulesvm.moduleImages = images;
        return modulesvm.moduleImages;
      }

      function error() {
        console.log('cannot get modules images');
      }
    }

    // Ajout d'un module
    function addModule(applicationName, imageName) {
      return ModuleService.addModule(applicationName, imageName);
    }

    // Suppression d'un module
    function removeModule(applicationName, moduleName) {
      return ModuleService.removeModule(applicationName, moduleName);
    }

  }
})();
