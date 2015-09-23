(function () {
  'use strict';
  angular
    .module('webuiApp.account')
    .controller('AccountLogsCtrl', AccountLogsCtrl);

  AccountLogsCtrl.$inject = ['AdminService',
    '$stateParams',
    'ErrorService'
  ];

  function AccountLogsCtrl(AdminService, $stateParams, ErrorService) {
    var vm = this;
    vm.user = {
      login: $stateParams.login
    };

    vm.events = [];
    vm.rows = 100000;

    vm.currentPage = 1;
    vm.pageSize = 15;

    init();

    function init() {
      AdminService.getUserLogs(vm.rows, $stateParams.login)
        .then(success)
        .catch(error);

      function success(events) {
        vm.events = events.data;
        return vm.events;
      }

      function error(response) {
        ErrorService.handle(response);
      }
    }
  }
})();

