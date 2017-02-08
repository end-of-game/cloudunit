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
    .component('accountRegistry', accountRegistry());

  function accountRegistry(){
    return {
      templateUrl: 'scripts/components/account/registry/account.admin.registry.html',
      bindings: {},
      controller: [
        'AdminService',
        'ErrorService',
        AccountRegistryCtrl
      ],
      controllerAs: 'accountImage',
    };
  }

  function AccountRegistryCtrl(AdminService, ErrorService) {
    
    var vm = this;
		vm.saveRegistryConf = saveRegistryConf;

    vm.$onInit = function() {

    }

		/////////////////////////////////////////////

		function saveRegistryConf(image) {
			AdminService.enable ( image.name )
        .then ( function(response) {
            cleanMessage();
            vm.manageNoticeMsg = 'registry information registred !';
        }).catch (function(response) {
          cleanMessage();
          if(response.data.message) {
            vm.manageErrorMsg = response.data.message;
          } else {
            vm.manageErrorMsg = 'An error has been encountered !';
          };  
        });
		}

    function cleanMessage() {
      vm.errorMsg = '';
      vm.noticeMsg = '';
    }
		
  }
})();
