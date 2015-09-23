/**
 * @ngdoc service
 * @name webuiApp.UserService
 * @description # UserService Factory in the webuiApp.
 */


(function () {

  'use strict';

  angular
    .module('webuiApp')
    .factory('UserService', UserService);

  UserService.$inject = [
    '$cookieStore',
    '$http',
    '$interval',
    'ErrorService',
    '$rootScope'
  ];

//////////////////////////////////////////

  function UserService($cookieStore, $http, $interval, ErrorService, $rootScope) {

    var user = {};
    user.check = check;
    user.profile = profile;
    user.logout = logout;
    user.createLocalSession = createLocalSession;
    user.deleteLocalSession = deleteLocalSession;
    user.isLogged = isLogged;
    user.getRole = getRole;
    user.checkUserStatus = checkUserStatus;

    return user;

    function check(username, password) {

      var data, headers;

      data = 'j_username=' + username + '&j_password=' + password + '&submit=Login';

      headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json, text/plain, */*'
      };

      return $http.post('/user/authentication', data, {
        headers: headers
      });
    }

    function profile() {
      return $http.get('/user/status', {});
    }

    function logout() {
      deleteLocalSession();
      return $http.get('/user/logout', {});
    }

    function createLocalSession() {
      $cookieStore.put('isLogged', 'true');
    }

    function deleteLocalSession() {
      $cookieStore.remove('isLogged');
    }

    function isLogged() {
      return $cookieStore.get('isLogged') !== undefined;
    }

    function getRole() {
      profile().then(function(user){
        return user.data.role.description;
      });
    }

    function checkUserStatus(){
      getUserStatus();
      return $interval(function(){
        getUserStatus();
      }, 2000);
    }

    function getUserStatus() {
      profile()
        .then(function (user) {
          // si le statut est à 3 on gel l'IHM
          if(user.data.status === 3){
            $rootScope.$broadcast(':freezeSystem');
          } else {
            $rootScope.$broadcast(':unFreezeSystem');
          }
        })
        .catch(function (error) {
          ErrorService.handle(error);
        });
    }
  }
})();




