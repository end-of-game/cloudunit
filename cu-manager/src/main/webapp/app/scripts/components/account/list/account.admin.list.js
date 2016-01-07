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
    .module ( 'webuiApp.account' )
    .config ( function ( paginationTemplateProvider ) {
      paginationTemplateProvider.setPath ( 'scripts/shared/pagination/pagination.html' );
    } )
    .value ( 'statuses', [
        { name: 'All', value: '' },
        { name: 'Normal', value: 'normal' },
        { name: 'Low', value: 'low' },
        { name: 'Inactive', value: 'none' }
      ]
    )
    .directive ( 'accountList', AccountList );

  function AccountList () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/account/list/account.admin.list.html',
      scope: {
        users: '=',
        roles: '=',
        onDelete: '&',
        onChangeRole: '&',
        selectedRole: '@',
        error: '='
      },
      controller: [
        'statuses',
        AccountListCtrl
      ],
      controllerAs: 'accountAdminList',
      bindToController: true
    };
  }

  function AccountListCtrl ( statuses ) {
    var vm = this;
    vm.statuses = statuses;
    vm.selectedStatus = vm.statuses[0];

    vm.currentPage = 1;
    vm.pageSize = 10;

    vm.handleDeleteUser = function ( user ) {
      vm.onDelete ( { user: user } );
    };

    vm.handleChangeRole = function ( user, role ) {
      vm.onChangeRole ( { user: user, role: role } );
    }
  }
}) ();

