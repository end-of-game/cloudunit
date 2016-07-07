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
    .component ( 'accountAdmin', AccountAdmin() );

  function AccountAdmin () {
    return {
      templateUrl: 'scripts/components/account/account.admin.html',
      scope: {},
      controller: [
        '$scope',
        'AdminService',
        AccountAdminCtrl
      ],
      controllerAs: 'accountAdmin',
    };
  }

  function AccountAdminCtrl ( $scope, adminService ) {

    var vm = this;

    vm.users = [];

    vm.user = {
      email: '',
      firstName: '',
      lastName: '',
      login: '',
      organization: '',
      password: ''
    };

    vm.roles = ['user', 'admin'];

    vm.deleteUser = deleteUser;
    vm.createUser = createUser;
    vm.changeRole = changeRole;
    vm.errorMsg = "";
    
    vm.$onInit = function() {
      getUsers ();      
    }

    //////////////////////////////////////////

    function deleteUser ( user ) {
      adminService.deleteUser ( user.login ).then ( function () {
        getUsers ();
      } ).catch ( function ( error ) {
        vm.errorMsg = error.data.message;
        return vm.errorMsg;
      } );
    }

    function createUser ( user ) {
      adminService.createUser ( user )
        .then ( success )
        .catch ( error );

      function success () {
        $scope.$broadcast ( ':formSuccess' ); // cf. closeModalDirective
        resetForm ();
        getUsers ();
      }

      function error ( response ) {
        $scope.$broadcast ( ':formError' ); // cf. closeModalDirective
        vm.errorMsg = response.data.message;
        return vm.errorMsg;
      }
    }

    function getUsers () {
      return adminService.getUsers ()
        .then ( success );

      function success ( users ) {
        vm.users = users;
        return vm.users;
      }
    }

    function changeRole ( user, role ) {
      adminService.changeRole ( user, role ).then ( function () {
        getUsers ();
      } ).catch ( function ( error ) {
        vm.errorMsg = error.data.message;
        return vm.errorMsg;
      } );
    }

    function resetForm () {
      vm.user = {
        email: '',
        firstName: '',
        lastName: '',
        login: '',
        organization: '',
        password: ''
      };
      $scope.createUserForm.$setPristine ();
    }
  }
}) ();




