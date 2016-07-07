// jscs:disable safeContextKeyword
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
   * @ngdoc function
   * @name webuiApp.controller:AuthCtrl
   * @description # AuthCtrl Controller of the webuiApp
   */
  angular
    .module('webuiApp.login')
    .component('login', Login());

  function Login() {
    return {
      templateUrl: 'scripts/components/login/login.html',
      bindings: {
        cuEnv: '=',
        errorMsg: '=',
      },
      controller: [
        '$scope',
        'UserService',
        'ErrorService',
        LoginCtrl,
      ],
      controllerAs: 'login',
    };
  }

  function LoginCtrl($scope, UserService, ErrorService) {

    var vm = this;
    vm.user = {
      username: '',
      password: '',
    };
    vm.discardMsg = discardMsg;
    vm.check = check;

    function reset() {
      vm.user = {
        username: '',
        password: '',
      };
    }

    function check(username, password) {

      return UserService.check(username, password)
        .then(onSuccess)
        .catch(onError);

      function onSuccess() {
        UserService.createLocalSession();
        reset();
        $scope.$emit(':loginSuccess');
      }

      function onError(response) {
        ErrorService.handle(response);
      }
    }

    function discardMsg() {
      vm.errorMsg = null;
    }
  }
}());

