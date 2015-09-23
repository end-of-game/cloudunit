'use strict';

angular.module('webuiApp')
  .config([
    '$stateProvider',
    '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
      //
      // For any unmatched url, redirect to /state1
      $urlRouterProvider.otherwise('/dashboard');
      //
      // Now set up the states
      // Use resolve property to make sure data is loaded before
      // controller
      $stateProvider
        .state('login', {
          url: '/login',
          templateUrl: 'views/login.html',
          controller: 'LoginCtrl',
          controllerAs: 'login',
          data: {
            isFree: true
          }
        }).state('dashboard', {
          url: '/dashboard',
          templateUrl: 'views/dashboard.html',
          controller: 'DashboardCtrl',
          controllerAs: 'dashboard',
          data: {
            isFree: false
          }
        }).state('snapshot', {
          url: '/snapshot',
          templateUrl: 'views/snapshot.html',
          controller: 'SnapshotCtrl',
          controllerAs: 'snapshot',
          data: {
            isFree: false
          }
        })
        .state('editApplication',
        {
          url: '/editApplication/:name/:tab',
          templateUrl: 'views/editApplication.html',
          controller: 'EditApplicationCtrl',
          controllerAs: 'editApp',
          resolve: {
            CurrentApplicationName: function ($stateParams) {
              return $stateParams.name;
            },
            CurrentApplication: function (ApplicationService, $stateParams) {
              return ApplicationService.findByName($stateParams.name);
            }
          },
          data: {
            isFree: false
          }
        })
        .state('account', {
          abstract: true,
          url: '/account',
          template: '<ui-view/>'
        })
        .state('account.admin', {
          url: '/admin',
          templateUrl: 'views/account.admin.html',
          controller: 'AccountAdminCtrl',
          controllerAs: 'accountAdmin',
          data: {
            isFree: false
          }
        })
        .state('account.logs', {
          url: '/logs/:login',
          templateUrl: 'views/account.admin.logs.html',
          controller: 'AccountLogsCtrl',
          controllerAs: 'accountLogs',
          data: {
            isFree: false
          }
        });
    }]);
