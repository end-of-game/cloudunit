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
    .component ( 'scripting', Scripting() );


  function Scripting () {
    return {
      templateUrl: 'scripts/components/scripting/scripting.html',
      bindings: {
        context: '='
      },
      controller: [
        '$scope',
        'ScriptingService',
        'ErrorService',
        ScriptingCtrl
      ],
      controllerAs: 'scripting',
    };
  }

  function ScriptingCtrl ( $scope, ScriptingService, ErrorService) {
        
    var vm = this;
    vm.currentPage = 1;
    vm.pageSize = 10;
    vm.title = '';
    vm.content = '';
    vm.scripts = [];
    /*    
      vm.date = 'recent';
        vm.orderByDate = true;*/
    vm.executeScript = executeScript;
    vm.editScript = editScript;
    vm.deleteScript = deleteScript;
    vm.addScript = addScript;



    vm.$onInit = function() {
        ScriptingService.getListScript()
        .then(success)
        .catch(error);

      function success(scripts) {
        vm.scripts = scripts;
        console.log(scripts);
      }

      function error(response) {
        ErrorService.handle(response);
      }  
    }

    ////////////////////////////////////////////////////

    function executeScript ( scriptContent ) {
      ScriptingService.executeScript ( scriptContent )
        .then ( function() {
          console.log("ok");
        } )
        .catch ( function() {
          console.log("nn");
        } );
    }

    function editScript ( scriptId, scriptContent, scriptTitle ) {
      ScriptingService.deleteScript ( scriptId, scriptContent, scriptTitle )
        .then ( function() {
          console.log("ok");
        } )
        .catch ( function() {
          console.log("nn");
        } );
    }

    function deleteScript ( scriptId ) {
      ScriptingService.deleteScript ( scriptId )
        .then ( function() {
          console.log("ok");
        } )
        .catch ( function() {
          console.log("nn");
        } );
    }

    function addScript ( scriptContent, scriptTitle ) {
      console.log('Add script');
      console.log(scriptContent, scriptTitle);
      ScriptingService.addScript ( scriptContent, scriptTitle )
        .then ( function() {
          vm.title = '';
          vm.content = '';

          console.log("ok");
        } )
        .catch ( function() {
          console.log("nn");
        } );
    }

  }
}) ();

