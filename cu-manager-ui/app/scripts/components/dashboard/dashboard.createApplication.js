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
    .component('createApp', CreateApp());

  function CreateApp(){
    return {
      templateUrl: 'scripts/components/dashboard/dashboard.createApplication.html',
      bindings: {
        app: '='
      },
      controller: [
        '$rootScope',
        'ApplicationService',
        'ImageService',
        'ErrorService',
        CreateAppCtrl
      ],
      controllerAs: 'createApp',
    };
  }

  function CreateAppCtrl($rootScope, ApplicationService, ImageService, ErrorService) {

    var vm = this;
    vm.applicationName = '';

    vm.notValidated = true;
    vm.message = '';
    vm.isPending = false;
    vm.createApplication = createApplication;
    vm.isValid = isValid;
       
    function createApplication(applicationName, serverName) {
      // emit app:creating event to display a shadow app during creation process
      vm.isPending = true;
      $rootScope.$broadcast('app:creating', applicationName);

      ApplicationService.create(applicationName, serverName)
        .then(success)
        .catch(error);

      function success() {
        // reset du  formulaire
        vm.createAppForm.$setPristine();
        vm.applicationName = '';
        vm.isPending = false;
        vm.serverImageSelect2 = undefined;
        setTimeout(function() {
          vm.serverImageChoice = vm.serverImages[0];
        }, 1);
      }

      function error(response) {
        vm.message = response.data.message;
        $rootScope.$broadcast('app:create:fail', applicationName);
      }
    }

    function isValid(applicationName, serverName) {
      if(!vm.app.filter(function(application) {return application.displayName === applicationName; }).length) {
        vm.notValidated = false;
        vm.message = '';
      } else {
          vm.message = 'Application\'s name already exists';
          vm.notValidated = true;
          vm.isPending = false;
      }
        // ApplicationService.isValid(applicationName, serverName)
        // .then(success)
        // .catch(error);

        // function success() {
        //   vm.notValidated = false;
        //   vm.message = '';
        // }

        // function error(response) {
        //   vm.message = response.data.message;
        //   vm.notValidated = true;
        //   vm.isPending = false;
        // }
    }
  }
})();
