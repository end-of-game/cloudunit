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

  /**
   * @ngdoc function
   * @name webuiApp.controller:MainCtrl
   * @description
   * # MainCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .directive('createApp', CreateApp);

  function CreateApp(){
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/dashboard/dashboard.createApplication.html',
      scope: {},
      controller: [
        'ApplicationService',
        'ImageService',
        'ErrorService',
        CreateAppCtrl
      ],
      controllerAs: 'createApp',
      bindToController: true
    };
  }

  function CreateAppCtrl(ApplicationService, ImageService, ErrorService) {

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


