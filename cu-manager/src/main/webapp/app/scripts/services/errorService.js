(function () {
  'use strict';

  angular
    .module('webuiApp')
    .factory('ErrorService', ErrorService);

  ErrorService.$inject = ['$rootScope'];


  function ErrorService($rootScope) {
    return {
      handle: handle
    };

    function handle(error) {
      if (error.status === 401) {
        $rootScope.$broadcast(':unauthorized', {
          message: 'The username or password you entered was incorrect'
        });
      }

      if(error.status === 500 || error.status === 404){
        $rootScope.$broadcast(':systemError', {
          message: 'Sorry, an internal error has occurred. Please login later or contact support'
        });
      }

      if (error.status === 403) {
        $rootScope.$broadcast(':forbidden', {
          message: 'Sorry, access to this page is restricted'
        });
      }
    }
  }
})();

