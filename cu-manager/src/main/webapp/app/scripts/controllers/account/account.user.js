(function () {
  'use strict';
  angular
    .module('webuiApp.account')
    .controller('AccountUserCtrl', AccountUserCtrl);
  AccountUserCtrl.$inject = ['user'];

  function AccountUserCtrl(user) {
    var vm = this;
    vm.user = user;
  }
})();




