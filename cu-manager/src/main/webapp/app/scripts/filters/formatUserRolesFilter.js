(function(){
  'use strict';

  angular
    .module('webuiApp.filters')
    .filter('formatUserRoles', formatUserRoles);

  function formatUserRoles() {
    return function (input) {
      var role = '';
      switch (input) {
        case 'ROLE_ADMIN':
          role = 'Admin';
          break;
        case 'ROLE_USER':
          role = 'User';
          break;
        default :
          role = 'Unknown';
          break;
      }
      return role;
    };
  }
})();



