(function () {
  'use strict';

  angular.module('webuiApp')
    .controller('ErrorCtrl', ErrorCtrl);

  ErrorCtrl.$inject = [
    '$location',
    'UserService'
  ];


  function ErrorCtrl($location, UserService) {
    var vm = this;

    vm.logout = logout;

    function logout() {
      UserService.logout();
      $location.path('/login');
    }
  }
})();





