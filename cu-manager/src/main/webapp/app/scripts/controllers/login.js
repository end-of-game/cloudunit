(function () {
  'use strict';

  /**
   * @ngdoc function
   * @name webuiApp.controller:AuthCtrl
   * @description # AuthCtrl Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .controller('LoginCtrl', LoginCtrl);

  LoginCtrl.$inject = [
    '$scope',
    'UserService',
    'ErrorService'
  ];

  function LoginCtrl($scope, UserService, ErrorService) {

    var vm = this;
    vm.user = {
      username: '',
      password: ''
    };

    vm.check = check;

    function reset() {
      vm.user = {
        username: '',
        password: ''
      };
    }

    function check(username, password) {
      return UserService.check(username, password)
        .then(success)
        .catch(error);

      function success() {
        UserService.createLocalSession();
        reset();
        $scope.$emit(':loginSuccess');
      }

      function error(response) {
        ErrorService.handle(response);
      }
    }

  }
}());





