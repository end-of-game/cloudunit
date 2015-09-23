(function () {
  'use strict';

  /**
   * @ngdoc function
   * @name webuiApp.controller:MainCtrl
   * @description
   * # MainCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .controller('QuickEditCtrl', QuickEditCtrl);

  QuickEditCtrl.$inject = [
    'ApplicationService'
  ];

  function QuickEditCtrl(ApplicationService) {
    var vm = this;

    vm.list = [];
    vm.getApplications = getApplications;

    getApplications();

    function getApplications() {
      ApplicationService.list().then(function (list) {
        vm.list = list;
      });
    }
  }
})();

