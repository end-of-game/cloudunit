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
    
		vm.pageSize = 10;
    vm.currentPage = 1;

		vm.predicate = 'name';
    vm.reverse = false;
    vm.order = order;

		vm.enableImage = enableImage;
		vm.disableImage = disableImage;
		vm.pullImage = pullImage;
		vm.removeImage = removeImage;


    vm.$onInit = function() {
			getListImage();
    }

		/////////////////////////////////////////////
		function getListImage() {
			ImageService.findAll()
				.then(function(images) {
					vm.listImages = images;
				});
		}

		function enableImage(image) {
			ImageService.enable ( image.name )
        .then ( function(response) {
            getListImage();
            cleanMessage();
            vm.manageNoticeMsg = 'image successfully enabled !';
        }).catch (function(response) {
          cleanMessage();
          if(response.data.message) {
            vm.manageErrorMsg = response.data.message;
          } else {
            vm.manageErrorMsg = 'An error has been encountered !';
          };  
        });
		}

		function disableImage(image) {
			ImageService.disable ( image.name )
        .then ( function(response) {
            getListImage();
            cleanMessage();
            vm.manageNoticeMsg = 'image successfully disabled !';
        }).catch (function(response) {
          cleanMessage();
          if(response.data.message) {
            vm.manageErrorMsg = response.data.message;
          } else {
            vm.manageErrorMsg = 'An error has been encountered !';
          };  
        });
		}
		
		function pullImage(image) {
      ImageService.pull(image)
        .then ( function(response) {
            getListImage();
            cleanMessage();
            vm.manageNoticeMsg = 'image successfully pulled !';
        }).catch (function(response) {
          cleanMessage();
          if(response.data.message) {
            vm.manageErrorMsg = response.data.message;
          } else {
            vm.manageErrorMsg = 'An error has been encountered !';
          };  
        });
		}

		function removeImage(image) {
			ImageService.remove ( image.id )
        .then ( function(response) {
            getListImage();
            cleanMessage();
            vm.manageNoticeMsg = 'image successfully removed !';
        }).catch (function(response) {
          cleanMessage();
          if(response.data.message) {
            vm.manageErrorMsg = response.data.message;
          } else {
            vm.manageErrorMsg = 'An error has been encountered maybe an application already use this image !';
          };  
        });
		}

    function cleanMessage() {
      vm.manageErrorMsg = '';
      vm.manageNoticeMsg = '';
    }
		
		function order (predicate) {
      vm.reverse = (vm.predicate === predicate) ? !vm.reverse : false;
      vm.predicate = predicate;
    }

  }
})();
