(function () {
  'use strict';

  /**
   * @ngdoc overview
   * @name webuiApp
   * @description # webuiApp
   *
   * Main module of the application.
   */
  angular
    .module('webuiApp',
    [
      // core modules
      'ngResource',
      'ngCookies',
      'ngSanitize',
      'ngAnimate',
      'ngRoute',
      'ui.router',
      'angularFileUpload',
      'ngTable',
      'ui.gravatar',
      'angularUtils.directives.dirPagination',
      'textAngular',
      'angular-timeline',
      'angular.filter',
      'ui.bootstrap',

      //shared modules
      'webuiApp.filters',
      'webuiApp.directives',

      // app areas
      'webuiApp.editApplication',
      'webuiApp.account'
    ])
    .constant('moment', moment)
    // moment locale config
    .config(function(){
      moment.locale('en', {
        calendar: {
          lastDay : '[Yesterday]',
          sameDay : '[Today]'
        }
      });
    });
})();



