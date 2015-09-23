(function(){
  'use strict';

  angular
    .module('webuiApp.filters')
    .filter('formatAppStatus', formatAppStatus);

  function formatAppStatus() {
    return function (input) {
      var status = '';
      switch (input) {
        case 'START':
          status = 'Start';
          break;
        case 'STOP':
          status = 'Stop';
          break;
        case 'PENDING':
          status = 'Processing';
          break;
        case 'FAIL':
          status = 'Error';
          break;
        default :
          status = 'Start';
          break;
      }
      return status;
    };
  }
})();



