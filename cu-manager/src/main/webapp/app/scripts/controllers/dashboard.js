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
    .controller('DashboardCtrl', DashboardCtrl);

  DashboardCtrl.$inject = [
    '$scope',
    '$interval',
    'ApplicationService',
    'ErrorService'
  ];


  function DashboardCtrl($scope, $interval, ApplicationService, ErrorService) {
    var timer, vm = this;
    vm.applications = [];
    vm.selectedItem = 'All';
    vm.search = '';
    vm.deleteApplication = deleteApplication;
    vm.toggleServer = toggleServer;

    // initialisation de la liste d'applications
    update();

    // polling sur la la méthode refresh
    timer = $interval(function () {
      update();
    }, 2000);


    // Pour des raisons de performance, arrête le polling sur la liste d'applications
    // lorsque le scope est détruit
    $scope.$on('$destroy', function () {
      $interval.cancel(timer);
    });

    /////////////////////////////////////////////


    // Rafraichi la liste d'applications
    function update() {
      ApplicationService.list()
        .then(success)
        .catch(error);

      function success(applications) {
        vm.applications = applications;
        return vm.applications;
      }

      function error(response) {
        ErrorService.handle(response);
        if(timer){
          $interval.cancel(timer);
        }
      }
    }

    // Suppression de l'application
    function deleteApplication(applicationName) {
      ApplicationService.remove(applicationName);
    }

    function toggleServer(application) {
      if (application.status === 'START') {
        stopApplication(application.name);
      } else if (application.status === 'STOP') {
        startApplication(application.name);
      }
    }

    // Démarrage de l'application
    function startApplication(applicationName) {
      ApplicationService.start(applicationName);
    }

    // Arrêt de l'application
    function stopApplication(applicationName) {
      ApplicationService.stop(applicationName);
    }
  }
})();

