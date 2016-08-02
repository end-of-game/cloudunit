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
    .component ( 'scriptingManage', Scripting() );


  function Scripting () {
    return {
      templateUrl: 'scripts/components/scripting/scripting.manage.html',
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
    vm.noticeMsg = '';
    vm.errorMsg = '';

    vm.title = '';
    vm.content = '';
    vm.scripts = [];

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
      }

      function error(response) {
        ErrorService.handle(response);
      }  
    }

    ////////////////////////////////////////////////////

    function executeScript ( scriptContent ) {
      ScriptingService.executeScript ( scriptContent )
        .catch ( function(response) {
          ErrorService.handle(response);
          vm.errorMsg = 'An error has been encountered!'
          vm.noticeMsg = '';
        } );
    }

    function editScript ( scriptId, scriptContent, scriptTitle ) {
      ScriptingService.editScript ( scriptId, scriptContent, scriptTitle )
        .catch ( function(response) {
          ErrorService.handle(response);
        } );
    }

    function deleteScript ( script ) {
     
      ScriptingService.deleteScript ( script.id )
        .then ( function() {
          vm.scripts.splice(vm.scripts.indexOf(script), 1);
        } )
        .catch ( function() {
          ErrorService.handle(response);
        } );
    }

    function addScript ( scriptContent, scriptTitle ) {
      ScriptingService.addScript ( scriptContent, scriptTitle )
        .then ( function(script) {
          vm.title = '';
          vm.content = '';
          vm.scripts.push(script);
          vm.noticeMsg = 'Script successfully created!';
          vm.errorMsg = '';
        } )
        .catch ( function(response) {
          ErrorService.handle(response);
          vm.errorMsg = 'An error has been encountered!';
          vm.noticeMsg = '';
        } );
    }
  }
}) ();
