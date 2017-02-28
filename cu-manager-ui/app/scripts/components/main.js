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

  angular
    .module('webuiApp')

    .controller('MainCtrl', MainCtrl);

  MainCtrl.$inject = [
    '$rootScope',
    'UserService',
    '$interval',
    '$state',
    'ApplicationService'
  ];

  function MainCtrl($rootScope, UserService, $interval, $state, ApplicationService) {

    var vm = this;
    var timer;
    vm.isFrozen = false;
    vm.isAdmin = false;
    vm.systemError = null;
    vm.isLogged = isLogged;
    vm.logout = logout;
    vm.browserOutdated = false;
    vm.CUEnv = '';
    vm.about = '';

    getUserRole();
    getCUEnv();
    getAbout();

    $rootScope.$on(':loginSuccess', function() {
      //reset error message
      vm.systemError = null;
      $state.go('dashboard');
    });

    $rootScope.$on(':freezeSystem', function() {
      vm.isFrozen = true;
    });

    $rootScope.$on(':unFreezeSystem', function() {
      vm.isFrozen = false;
    });

    $rootScope.$on(':systemError', function(e, data) {
      vm.systemError = data.message;
      logout();
    });

    $rootScope.$on(':unauthorized', function(e, data) {
      vm.systemError = data.message;
      logout();
    });

    $rootScope.$on(':forbidden', function(e, data) {
      vm.systemError = data.message;
      $state.go('dashboard');
    });

    $rootScope.$on('$stateChangeStart', function(event, toState) {
      vm.$state = toState;

      var restrictedArea = !toState.data.isFree;

      if (restrictedArea && !isLogged()) {
        logout();
      }

      // If user is authenticated, we redirect him
      if (toState.name === 'login' && isLogged()) {
        $state.go('dashboard');
      }

      if (toState.name !== 'login') {
        getUserRole();
        // TODO
        // if (!timer) {
        //   timer = UserService.checkUserStatus();
        // }
      }

      if (toState.name === 'login') {
        // full screen locking
        vm.isFrozen = false;
      }
    });

    function getUserRole() {
      if (isLogged()) {
        // TODO
        // UserService.profile().then(function(user) {
        //   vm.isAdmin = user.data.role.description === 'ROLE_ADMIN';
        // });
      }
    }

    function getCUEnv() {
      return UserService.getCUEnv().then(function success(response) {
        vm.CUEnv = response.cuInstanceName;
      });
    }

    function getAbout() {
      // TODO
      // ApplicationService.about().then(function (response) {
      //   vm.about = response;
      // });
    }

    function logout() {
      UserService.logout();
      $state.go('login');
      if (timer) {
        // Stop the polling
        $interval.cancel(timer);
        timer = null;
      }
    }

    function isLogged() {
      // TODO
      // return UserService.isLogged();
      return true;
    }
  }
})();

