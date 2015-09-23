(function () {
  'use strict';

  /**
   * @ngdoc function
   * @name webuiApp.controller:MainCtrl
   * @description
   * # MainCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .controller('CreateApplicationCtrl', CreateApplicationCtrl);

  CreateApplicationCtrl.$inject = [
    '$scope',
    'ApplicationService',
    'ImageService',
    'ErrorService'
  ];

  function CreateApplicationCtrl($scope, ApplicationService, ImageService, ErrorService) {

    var vm = this;
    vm.applicationName = '';
    vm.serverImages = [];
    vm.serverImageChoice = {};
    vm.notValidated = true;
    vm.message = '';

    vm.createApplication = createApplication;
    vm.isValid = isValid;

    init();

    function init() {
      ImageService.findEnabledServer()
        .then(success)
        .catch(error);

      function success(serverImages) {
        vm.serverImages = serverImages;
        vm.serverImageChoice = serverImages[2];
      }

      function error(response) {
        ErrorService.handle(response);
      }
    }

    function createApplication(applicationName, serverName) {
      // On demande une creation de l'application.
      // On est en mode non bloquant car 'create' utilise une promise
      ApplicationService.create(applicationName, serverName)
        .then(success)
        .catch(error);

      function success() {
        // reset du  formulaire
        vm.createAppForm.$setPristine();
        vm.applicationName = '';
      }

      function error(response) {
        vm.message = response.data.message;
      }
    }

    function isValid(applicationName, serverName) {
      // on désactive le bouton. On pourrait imaginer un spinner ou bien bloquer la routes
      ApplicationService.isValid(applicationName, serverName)
        .then(success)
        .catch(error);

      function success() {
        vm.notValidated = false;
        vm.message = '';
      }

      function error(response) {
        vm.message = response.data.message;
        vm.notValidated = true;
      }
    }
  }
})();


