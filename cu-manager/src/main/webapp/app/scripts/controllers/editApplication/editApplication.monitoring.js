(function () {
  'use strict';

  angular
    .module('webuiApp.editApplication')
    .controller('MonitoringCtrl', ApplicationMonitoringCtrl);

  ApplicationMonitoringCtrl.$inject = [
    'ApplicationService',
    '$stateParams'
  ];

  function ApplicationMonitoringCtrl(ApplicationService, $stateParams) {

    // ------------------------------------------------------------------------
    // MONITORING
    // ------------------------------------------------------------------------

    var vm = this;

    vm.containers = [];
    vm.myContainer = {};
    vm.isLoading = true;
    vm.getContainers = getContainers;

    getContainers();

    function getContainers(selectedContainer) {
      return ApplicationService.listContainers($stateParams.name)
        .then(function onGetContainersComplete(containers) {
          vm.containers = containers;
          vm.myContainer = selectedContainer || containers[0];
          vm.isLoading = false;
          return vm.containers;
        })
        .catch(function onGetContainersError(reason) {
          console.error(reason); //debug
        });
    }
  }
})();


