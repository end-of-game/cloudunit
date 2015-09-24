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

(function () {
  'use strict';
  angular
    .module('webuiApp.editApplication')
    .controller('EditSnapShotCtrl', EditSnapShotCtrl);

  EditSnapShotCtrl.$inject = ['SnapshotService'];

  function EditSnapShotCtrl(SnapshotService) {

    var vm = this;
    vm.message = "";
    vm.createNewSnapShot = createNewSnapShot;

    // Champ composant un nouveau snapshot
    vm.newSnapshot = {
      name: '',
      description: 'Enter a comment, your teammate will appreciate'
    };

    // Création d'un nouveau snapshot
    function createNewSnapShot(applicationName, snapshot) {
      SnapshotService.create(applicationName, snapshot)
        .then(success)
        .catch(error);

      function success() {
        // reset du formulaire
        vm.newSnapshot.name = '';
        vm.newSnapshot.description = 'Enter a comment, your teammate will appreciate';
      }

      function error(response) {
        if (response.data.message === 'You can\'t create a snapshot if your app is pending') {
          return;
        }
        vm.message = response.data.message;
      }
    }
  }
})();

