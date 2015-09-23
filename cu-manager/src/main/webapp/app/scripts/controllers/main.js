(function () {
  'use strict';

  angular
    .module('webuiApp')
    .controller('MainCtrl', MainCtrl);

  MainCtrl.$inject = [
    '$rootScope',
    'UserService',
    '$interval',
    '$state'
  ];

  function MainCtrl($rootScope, UserService, $interval, $state) {

    var vm = this, timer;
    vm.isFrozen = false;
    vm.isAdmin = false;
    vm.systemError = false;
    vm.isLogged = isLogged;
    vm.logout = logout;
    vm.browserOutdated = false;

    getUserRole();

    $rootScope.$on(':loginSuccess', function () {
      //reset error message
      vm.systemError = false;
      $state.go('dashboard');
    });

    $rootScope.$on(':freezeSystem', function () {
      vm.isFrozen = true;
    });

    $rootScope.$on(':unFreezeSystem', function () {
      vm.isFrozen = false;
    });

    $rootScope.$on(':systemError', function (e, data) {
      vm.systemError = data.message;
      logout();
    });

    $rootScope.$on(':unauthorized', function (e, data) {
      vm.systemError = data.message;
      logout();
    });

    $rootScope.$on(':forbidden', function (e, data) {
      vm.systemError = data.message;
      $state.go('dashboard');
    });

    $rootScope.$on('$stateChangeStart', function (event, toState) {
      var restrictedArea = !toState.data.isFree;

      if (restrictedArea && !isLogged()) {
        logout();
      }

      // on bloque la page login si l'utilisateur est authentifié
      if (toState.name === 'login' && isLogged()) {
        $state.go('dashboard');
      }

      if (toState.name !== 'login') {
        getUserRole();
        if (!timer) {
          // polling sur le statut de l'utilisateur, renvoie une promesse
          timer = UserService.checkUserStatus();
        }
      }

      if (toState.name === 'login') {
        // on ferme le screen lock sur la vue login
        vm.isFrozen = false;
      }
    });


    function getUserRole() {
      if (isLogged()) {
        UserService.profile().then(function (user) {
          vm.isAdmin = user.data.role.description === 'ROLE_ADMIN';
        });
      }
    }

    function logout() {
      UserService.logout();
      $state.go('login');
      if (timer) {
        // arrêt du polling sur le statut de l'utilisateur
        $interval.cancel(timer);
        timer = null;
      }
    }

    function isLogged() {
      return UserService.isLogged();
    }
  }
})();



