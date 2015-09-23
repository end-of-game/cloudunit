(function () {
  'use strict';

  angular
    .module('webuiApp')
    .factory('MessageService', MessageService);

  MessageService.$inject = ['$resource'];

  function MessageService($resource) {

    return {
      listMessages: listMessages,
      listMessagesForCurrentApplication: listMessagesForCurrentApplication
    };


    function listMessages() {
      var logs = $resource('messages/rows/10');
      return logs.query().$promise;
    }

    // Liste de toutes les images qui sont activés quelque soit leur type
    function listMessagesForCurrentApplication(application) {
      var logs = $resource('messages/application/' + application + '/rows/10');
      return logs.query().$promise;
    }
  }
})();



