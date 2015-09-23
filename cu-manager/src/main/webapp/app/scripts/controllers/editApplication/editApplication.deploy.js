(function () {
  "use strict";


  angular
    .module('webuiApp.editApplication')
    .controller('DeployCtrl', DeployCtrl);

  DeployCtrl.$inject = ['$scope', 'FileUploader'];

  function DeployCtrl($scope, FileUploader) {

    var uploader, currentApp, vm;

    vm = this;

    currentApp = $scope.editApp.application;

    vm.errors = {
      fileTypeError: false,
      sizeFilter: false,
      cancelMessage: false
    };

    uploader = $scope.uploader = new FileUploader({
      url: 'application/' + currentApp.name + '/deploy',
      removeAfterUpload: true,
      queueLimit: 1
    });

    // on verifie le statut de l'application: si elle est STOP on interdit l'upload

    // on vérifie que l'archive est au format autorisé
    uploader.filters.push({
      name: 'extensionFilter',
      fn: function (item /*{File|FileLikeObject}*/) {
        var authorizedTypes = ['ear', 'war'];
        var type = item.name.split('.');
        return authorizedTypes.indexOf(type[type.length - 1]) !== -1;
      }
    });

    // on verifie la taille du fichier: max 300000000 bytes soit 286 MB
    uploader.filters.push({
      name: 'sizeFilter',
      fn: function (item /*{File|FileLikeObject}*/) {
        return item.size < 300000000;
      }
    });

    // CALLBACKS

    uploader.onWhenAddingFileFailed = function (item /*{File|FileLikeObject}*/, filter, options) {
      if (filter.name === 'extensionFilter') {
        vm.errors.fileTypeError = true;
      }

      if (filter.name === 'sizeFilter') {
        vm.errors.sizeError = true;
      }
    };
    uploader.onAfterAddingFile = function (fileItem) {
      vm.errors.fileTypeError = false;
      vm.showMeta = true;
    };


    uploader.onCancelItem = function (fileItem, response, status, headers) {
      vm.errors.cancelMessage = true;
    };
  }
})();
