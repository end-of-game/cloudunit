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

  /**
   * @ngdoc function
   * @name webuiApp.controller:EditApplicationCtrl
   * @description
   * # EditApplicationCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp.editApplication')
    .directive('editApplication', EditApplication);

  function EditApplication() {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/editApplication/editApplication.html',
      scope: {
        state: '=',
      },
      controller: [
        '$rootScope',
        '$scope',
        '$stateParams',
        'ApplicationService',
        '$state',
        EditApplicationCtrl,
      ],
      controllerAs: 'editApp',
      bindToController: true,
    };
  }

  function EditApplicationCtrl($rootScope, $scope, $stateParams, ApplicationService, $state) {

    // ------------------------------------------------------------------------
    // SCOPE
    // ------------------------------------------------------------------------

    var vm = this;
    vm.monitoringRoute = false;

    vm.applicationService = ApplicationService;
    
    function refreshRoute() {
      if (($state.current.name == "editApplication.monitoring")
        || ($state.current.name == "editApplication.monitoringApp")) {
        vm.monitoringRoute = true;
      } else {
        vm.monitoringRoute = false;
      }
    }
    
    init();

    function init() {
      refreshRoute();
    }
    
    vm.applicationService.init($stateParams.name).then(function() {
      vm.application = vm.applicationService.state;
      $rootScope.$broadcast('application:ready', {
          app: vm.application,
        });
    });

    // We must destroy the polling when the scope is destroyed
    $scope.$on('$destroy', function() {
      vm.applicationService.stopPolling();
    });
    

    vm.updateRoute = function () { 
      
      setTimeout(function() {
        refreshRoute();
      }, 0);
    }
  }
})();


