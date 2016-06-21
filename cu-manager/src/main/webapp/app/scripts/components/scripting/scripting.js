/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
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

(function () {
  'use strict';

  /**
   * @ngdoc function
   * @name webuiApp.controller:ScriptingCtrl
   * @description
   * # ScriptingCtrl
   * Controller of the webuiApp
   */
  angular
    .module ( 'webuiApp.scripting' )
    .directive ( 'scripting', Scripting );


  function Scripting () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/scripting/scripting.html',
      scope: {
        context: '='
      },
      controller: [
        '$scope',
        '$stateParams',
        'ApplicationService',
        ScriptingCtrl
      ],
      controllerAs: 'scripting',
      bindToController: true
    };
  }

  function ScriptingCtrl ( $scope, $stateParams, ApplicationService) {
        
        var vm = this;

        vm.date = 'recent';
        vm.orderByDate = true;

        vm.executeScript = executeScript;


        init();

        ////////////////////////////////////////////////////

        function init() {
          console.log("hello");
        }

        function executeScript ( scriptContent ) {
          ApplicationService.executeScript ( scriptContent )
            .then ( function() {
              console.log("ok");
            } )
            .catch ( function() {
              console.log("nn");
            } );
        }
  }
}) ();

