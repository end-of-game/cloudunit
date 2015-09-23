(function () {
  'use strict';
  angular
    .module('webuiApp.account')
    .config(function (paginationTemplateProvider) {
      paginationTemplateProvider.setPath('views/pagination.html');
    })
    .value('statuses', [
      {name: 'All', value: ''},
      {name: 'Normal', value: 'normal'},
      {name: 'Low', value: 'low'},
      {name: 'Inactive', value: 'none'}
    ]
  )
    .controller('AccountListCtrl', AccountListCtrl);

  AccountListCtrl.$inject = ['statuses'];

  function AccountListCtrl(statuses) {
    var vm = this;
    vm.statuses = statuses;
    vm.selectedStatus = vm.statuses[0];

    vm.currentPage = 1;
    vm.pageSize = 10;

  }
})();

