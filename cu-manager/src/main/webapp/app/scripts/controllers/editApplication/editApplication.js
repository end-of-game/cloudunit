(function () {
  'use strict';

  /**
   * @ngdoc function
   * @name webuiApp.controller:EditApplicationCtrl
   * @description
   * # EditApplicationCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp.editApplication')
    .controller('EditApplicationCtrl', EditApplicationCtrl);

  EditApplicationCtrl.$inject = [
    '$scope',
    '$interval',
    'ApplicationService',
    'CurrentApplication',
    'CurrentApplicationName',
    '$stateParams',
    'ErrorService'
  ];

  function EditApplicationCtrl($scope, $interval, ApplicationService, CurrentApplication, CurrentApplicationName, $stateParams, ErrorService) {

    // ------------------------------------------------------------------------
    // DEFINITION DU SCOPE
    // ------------------------------------------------------------------------

    var timer, vm = this;

    vm.application = CurrentApplication;
    vm.currentTab = $stateParams.tab;

    // Methode principale chargée de recharger le détail de l'application
    update();

    // polling sur la la méthode refresh
    timer = $interval(function () {
      update();
    }, 2000);


    // Pour des raisons de performance, arrête le polling
    // lorsque le scope est détruit
    $scope.$on('$destroy', function () {
      $interval.cancel(timer);
    });

    /////////////////////////////////////////////////////

    function update() {
      ApplicationService.findByName(CurrentApplicationName)
        .then(success)
        .catch(error);

      function success(application) {
        vm.application = application;
        return vm.application;
      }

      function error(response) {
        ErrorService.handle(response);
      }
    }

  }
})();

