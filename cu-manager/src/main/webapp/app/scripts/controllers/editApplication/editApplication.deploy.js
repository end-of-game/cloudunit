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


    // To verify the format
    uploader.filters.push({
      name: 'extensionFilter',
      fn: function (item /*{File|FileLikeObject}*/) {
        var authorizedTypes = ['ear', 'war'];
        var type = item.name.split('.');
        return authorizedTypes.indexOf(type[type.length - 1]) !== -1;
      }
    });

    // To verify the file size
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
