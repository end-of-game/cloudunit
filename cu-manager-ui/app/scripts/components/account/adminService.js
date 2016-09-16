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

  angular
    .module('webuiApp')
    .factory('AdminService', AdminService);

  AdminService.$inject = [
    '$http',
    'moment',
    'ErrorService',
  ];

  //////////////////////////////////////////

  function AdminService($http, moment, ErrorService) {
    return {
      getUsers: getUsers,
      createUser: createUser,
      deleteUser: deleteUser,
      changeRole: changeRole,
      getUserLogs: getUserLogs,
      changePassword: changePassword,
    };

    function changePassword(oldPassword, newPassword) {
      var data = {
        password: oldPassword,
        newPassword: newPassword,
      };

      return $http.put('/user/change-password', data);
    }

    function getUsers() {
      var _users = [];
      return $http.get('/admin/users')
        .then(success)
        .catch(error);

      function success(users) {
        angular.forEach(users.data, function(user) {
          _setUserActivity(user);
          user.signin = _formatSigninDate(user.signin);
          _users.push(user);
        });

        return _users;
      }

      function error(response) {

        ErrorService.handle(response);
      }
    }

    function createUser(user) {
      return $http.post('/admin/user', user);
    }

    function deleteUser(userLogin) {
      return $http.delete('/admin/user/' + userLogin);
    }

    function changeRole(user, role) {
      var data = {
        login: user.login,
        role: role,
      };
      return $http.post('/admin/user/rights', data);
    }

    function _setUserActivity(user) {
      angular.extend(user, {
        activity: _activityFilter(user.lastConnection),
      });
      return user;
    }

    function getUserLogs(rows, login) {
      return $http.get('/admin/messages/rows/' + rows + '/login/' + login);
    }

    function _activityFilter(lastConnection) {
      var activity = 'normal';
      var period = _getDuration(lastConnection);

      if (period === null || period > 60) {
        activity = 'none';
      } else if (period > 30 && period < 60) {
        activity = 'low';
      }

      return activity;
    }

    function _getDuration(date) {
      if (date !== null) {
        return moment().diff(moment(date), 'days');
      } else {
        return date;
      }
    }

    function _formatSigninDate(date) {
      return moment(date).toDate();
    }
  }
})();

