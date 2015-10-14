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
  angular.module('webuiApp')
    .controller('MessageCtrl', MessageCtrl);

  MessageCtrl.$inject = [
    '$scope',
    '$location',
    '$interval',
    'MessageService',
    '$stateParams',
    'ErrorService'
  ];

  function MessageCtrl($scope, $location, $interval, MessageService, $stateParams, ErrorService) {
    var timer, currentApp, isDashboard, vm;
    currentApp = $stateParams.name;
    isDashboard = $location.path() === '/dashboard';
    vm = this;

    vm.messages = {};


    init();

    timer = $interval(function () {
      return (isDashboard) ? updateMessages() : updateMessagesForCurrentApplication();
    }, 2000);


    $scope.$on('$destroy', function () {
      $interval.cancel(timer);
    });

    ////////////////////////////////////////////////////

    function init() {
      return (isDashboard) ? updateMessages() : updateMessagesForCurrentApplication();
    }

    function updateMessages() {
      MessageService.listMessages()
        .then(success)
        .catch(error);

      function success(messages) {
        vm.messages = messages;
        return vm.messages;
      }

      function error(response) {
        ErrorService.handle(response);
        if(timer){
          $interval.cancel(timer);
        }
      }
    }

    function updateMessagesForCurrentApplication() {
      if(!currentApp){
        return;
      }
      MessageService.listMessagesForCurrentApplication(currentApp)
        .then(success)
        .catch(error);

      function success(messages) {
        vm.messages = messages;
        return vm.messages;
      }

      function error(response) {
        ErrorService.handle(response);
        if(timer){
          $interval.cancel(timer);
        }
      }
    }
  }
})();




