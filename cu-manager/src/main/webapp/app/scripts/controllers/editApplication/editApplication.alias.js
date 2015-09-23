(function () {
  'use strict';
  angular
    .module('webuiApp.editApplication')
    .controller('AliasCtrl', AliasCtrl);

  AliasCtrl.$inject = ['ApplicationService'];

  function AliasCtrl(ApplicationService) {

    var vm = this;
    vm.domain = '';
    vm.errorMsg = '';
    vm.createAlias = createAlias;
    vm.removeAlias = removeAlias;

    function createAlias(applicationName, domain) {
      ApplicationService.createAlias(applicationName, domain)
        .then(success)
        .catch(error);

      function success() {
        vm.errorMsg = '';
        vm.domain = '';
      }


      function error(response) {
        vm.errorMsg = response.data.message;
        return vm.errorMsg;
      }
    }
    function removeAlias(applicationName, domain) {
      ApplicationService.removeAlias(applicationName, domain);
    }
  }
})();
