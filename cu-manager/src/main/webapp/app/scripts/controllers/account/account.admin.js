(function () {
  'use strict';
  angular
    .module('webuiApp.account')
    .controller('AccountAdminCtrl', AccountAdminCtrl);

  AccountAdminCtrl.$inject = [
    '$scope',
    'AdminService'
  ];

  function AccountAdminCtrl($scope, adminService) {

    var vm = this;

    vm.users = [];

    vm.user = {
      email: '',
      firstName: '',
      lastName: '',
      login: '',
      organization: '',
      password: ''
    };

    vm.roles = ['user', 'admin'];

    vm.deleteUser = deleteUser;
    vm.createUser = createUser;
    vm.changeRole = changeRole;
    vm.errorMsg = "";

    getUsers();

    //////////////////////////////////////////

    function deleteUser(user) {
      adminService.deleteUser(user.login).then(function () {
        getUsers();
      }).catch(function (error) {
        vm.errorMsg = error.data.message;
        return vm.errorMsg;
      });
    }

    function createUser(user) {
      adminService.createUser(user)
        .then(success)
        .catch(error);

      function success() {
        $scope.$broadcast(':formSuccess'); // cf. closeModalDirective
        resetForm();
        getUsers();
      }

      function error(response) {
        $scope.$broadcast(':formError'); // cf. closeModalDirective
        vm.errorMsg = response.data.message;
        return vm.errorMsg;
      }
    }

    function getUsers() {
      return adminService.getUsers()
        .then(success);

      function success(users) {
        vm.users = users;
        return vm.users;
      }
    }

    function changeRole(user, role) {
      adminService.changeRole(user, role).then(function () {
        getUsers();
      }).catch(function (error) {
        vm.errorMsg = error.data.message;
        return vm.errorMsg;
      });
    }

    function resetForm() {
      vm.user = {
        email: '',
        firstName: '',
        lastName: '',
        login: '',
        organization: '',
        password: ''
      };
      $scope.createUserForm.$setPristine();
    }
  }
})();




