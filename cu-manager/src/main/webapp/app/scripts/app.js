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

(function() {
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
      'ui.router',
      'angularFileUpload',
      'ngTable',
      'ui.gravatar',
      'angularUtils.directives.dirPagination',
      'textAngular',
      'angular-timeline',
      'angular.filter',
      'ui.bootstrap',
      'angular-chartist',
      'nvd3',
      'metricsgraphics',
      'ui.codemirror',
      'ngclipboard',
      'xeditable',

      //shared modules
      'webuiApp.filters',
      'webuiApp.directives',

      // app areas
      'webuiApp.login',
      'webuiApp.dashboard',
      'webuiApp.editApplication',
      'webuiApp.snapshots',
      'webuiApp.mainTimeline',
      'webuiApp.scripting',
      'webuiApp.account',
      'webuiApp.feed',
      'webuiApp.tags',
    ])
    .config([
      '$urlRouterProvider',
      function($urlRouterProvider) {
        $urlRouterProvider.otherwise('/login');
      },])
    .constant('moment', moment)
    // moment locale config
    .config(function() {
      moment.locale('en', {
        calendar: {
          lastDay: '[Yesterday]',
          sameDay: '[Today]',
        },
      });
    })
    .constant('CONFIG', {
     dislayJolokia: true,
    });
})();

