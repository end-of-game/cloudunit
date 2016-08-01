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
    .component('accountProfil', accountProfil());

  function accountProfil(){
    return {
      templateUrl: 'scripts/components/account/profil/account.admin.profil.html',
      bindings: {},
      controller: [
        'AdminService',
        'ErrorService',
        AccountProfilCtrl
      ],
      controllerAs: 'accountProfil',
    };
  }

  function AccountProfilCtrl(AdminService, ErrorService) {
    
    var vm = this;

    // Config JVM
    vm.oldPassword = '';
    vm.newPassword = '';
    vm.confirmPassword = '';
    vm.errorMsg = '';
    vm.noticeMsg = '';

    vm.changePassword = changePassword;

    // Function to change password account  
    function changePassword(oldPassword, newPassword, confirmPassword) {
      console.log(oldPassword + '  ' + newPassword + '  ' + confirmPassword)
      if(newPassword !== confirmPassword) {
        vm.errorMsg = "Confirmation password error!";
      } else {
        AdminService.changePassword(oldPassword, newPassword)
          .then(function(res) {
            vm.noticeMsg = 'Password successfully update!';
            vm.errorMsg = '';
          }).catch(function(err) {
            vm.errorMsg = 'Your current password is not correct. Please retry!';
            vm.noticeMsg = '';
          });
      }

    }

  }
})();

