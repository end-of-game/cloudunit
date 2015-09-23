(function(){
  "use strict";
  angular.module('webuiApp.editApplication')
    .controller('OverviewCtrl', OverviewCtrl);

  OverviewCtrl.$inject = [
    '$scope',
    'ApplicationService',
    '$filter'];

  function OverviewCtrl($scope, ApplicationService, $filter){

    var vm = this;

    vm.toggleServer = toggleServer;
    vm.getTplUrl = getTplUrl;

    ///////////////////////////////////////////

    function toggleServer(application) {
      if (application.status === 'START') {
        stopApplication(application.name)
      } else if (application.status === 'STOP') {
        startApplication(application.name);
      }
    }

    // Démarrage de l'application
    function startApplication(applicationName) {
      ApplicationService.start(applicationName);
      $scope.$emit('workInProgress', {delay: 3000});
    }

    // Arrêt de l'application
    function stopApplication(applicationName) {
      ApplicationService.stop(applicationName);
      $scope.$emit('workInProgress', {delay: 3000});
    }

    // construction dynamique du nom du template de module pour le ng-include
    function getTplUrl(tpl){
      var moduleName = $filter('truncatestringfilter')(tpl);
      return 'views/_' + moduleName + '-module.html';
    }
  }
})();
