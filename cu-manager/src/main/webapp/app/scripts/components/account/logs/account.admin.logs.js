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

(function () {
  'use strict';
  angular
    .module('webuiApp.account')
    .directive('accountLogs', AccountLogs);

  function AccountLogs(){
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/account/logs/account.admin.logs.html',
      scope: {},
      controller: [
        'AdminService',
        '$stateParams',
        'ErrorService',
        AccountLogsCtrl
      ],
      controllerAs: 'accountAdminLogs',
      bindToController: true
    };
  }

  function AccountLogsCtrl(AdminService, $stateParams, ErrorService) {
    var vm = this;
    vm.user = {
      login: $stateParams.login
    };

    vm.events = [];
    vm.rows = 100000;

    vm.currentPage = 1;
    vm.pageSize = 15;

    init();

    function init() {
      AdminService.getUserLogs(vm.rows, $stateParams.login)
        .then(success)
        .catch(error);

      function success(events) {
        vm.events = events.data;
        return vm.events;
      }

      function error(response) {
        ErrorService.handle(response);
      }
    }
  }
})();

