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


    // Pour des raisons de performance, arrête le polling sur la liste de messages
    // lorsque le scope est détruit
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




