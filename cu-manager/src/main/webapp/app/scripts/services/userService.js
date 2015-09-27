/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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
      $rootScope.authenticated = true;
    }

    function deleteLocalSession() {
      $rootScope.authenticated = false;
    }

    function isLogged() {
      console.log("$rootScope.authenticated: " + $rootScope.authenticated);
      return $rootScope.authenticated;
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




