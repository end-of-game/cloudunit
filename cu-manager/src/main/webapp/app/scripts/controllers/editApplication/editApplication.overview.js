/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

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
