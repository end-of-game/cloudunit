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
    .component('accountImage', accountImage());

  function accountImage(){
    return {
      templateUrl: 'scripts/components/account/image/account.admin.image.html',
      bindings: {},
      controller: [
        'AdminService',
        'ErrorService',
				'ImageService',
        AccountImageCtrl
      ],
      controllerAs: 'accountImage',
    };
  }

  function AccountImageCtrl(AdminService, ErrorService, ImageService) {
    
    var vm = this;
    
		vm.pageSize = 5;
    vm.currentPage = 1;

		vm.predicate = 'name';
    vm.reverse = false;
    vm.order = order;

		vm.enableImage = enableImage;
		vm.disableImage = disableImage;
		vm.pullImage = pullImage;
		vm.removeImage = removeImage;


    vm.$onInit = function() {
			ImageService.findAll()
				.then(function(images) {
					vm.listImages = images;
				})
				.catch ( function (response) {
        	ErrorService.handle(response);
        });
    }

		/////////////////////////////////////////////
		
		function enableImage(image) {
			console.log('enable', image);
		}

		function disableImage(image) {
			console.log('disable', image);
		}
		
		function pullImage(image) {
			console.log('pullImage', image);
		}

		function removeImage(image) {
			console.log('remove', image);
		}

		function order (predicate) {
      vm.reverse = (vm.predicate === predicate) ? !vm.reverse : false;
      vm.predicate = predicate;
    }

  }
})();
