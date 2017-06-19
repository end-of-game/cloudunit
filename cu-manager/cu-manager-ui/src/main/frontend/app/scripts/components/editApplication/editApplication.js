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
    .component('editApplication', EditApplication());

  function EditApplication() {
    return {
      templateUrl: 'scripts/components/editApplication/editApplication.html',
      bindings: {
        state: '=',
      },
      controller: [
        '$rootScope',
        '$stateParams',
        'ApplicationService',
        '$state',
        'CONFIG',
        EditApplicationCtrl,
      ],
      controllerAs: 'editApp',
    };
  }

  function EditApplicationCtrl($rootScope, $stateParams, ApplicationService, $state, CONFIG) {

    // ------------------------------------------------------------------------
    // SCOPE
    // ------------------------------------------------------------------------
    
    var vm = this;
    vm.quickAccessNotice = ''
    vm.monitoringRoute = false;
    vm.settingsRoute = false;
    vm.currentServer = '';
    vm.dislayJolokia = CONFIG.dislayJolokia;
    vm.applicationService = ApplicationService;
    vm.hasTomcat = false;
    vm.hasTomcatMonitoring = false;
    
    function refreshRoute() {
      if (($state.current.name == "editApplication.monitoringContainers")
        || ($state.current.name == "editApplication.monitoringApplication")) {
        vm.monitoringRoute = true;
      } else {
        vm.monitoringRoute = false;
      }
      if (($state.current.name == "editApplication.settingsEnvironment")
        || ($state.current.name == "editApplication.settingsPort")
        || ($state.current.name == "editApplication.settingsJVM")
        || ($state.current.name == "editApplication.settingsAlias")) {
        vm.settingsRoute = true;
      } else {
        vm.settingsRoute = false;
      }
    }

    vm.$onInit = function() {
      refreshRoute();
    }
    
    vm.applicationService.init($stateParams.name).then(function() {
      vm.application = vm.applicationService.state;
      // vm.currentServer = vm.applicationService.state.server.image.displayName;
      vm.monitoringApplicationMenu();
      $rootScope.$broadcast('application:ready', {
          app: vm.application,
        });
    });

    // We must destroy the polling when the scope is destroyed
     vm.$onDestroy = function () {
      vm.applicationService.stopPolling();
    };
    

    vm.updateRoute = function () { 
      
      setTimeout(function() {
        refreshRoute();
      }, 0);
    }

    vm.monitoringApplicationMenu = function() {
      vm.hasTomcat = false;
      vm.hasTomcatMonitoring = false;

      if(vm.currentServer.indexOf('Tomcat') !== -1) {
        vm.hasTomcat = true;
      }

      if(vm.currentServer.indexOf('Tomcat 7') !== -1 || vm.currentServer.indexOf('Tomcat 8') !== -1) {
        vm.hasTomcatMonitoring = true;
      }
    }
  }
})();


