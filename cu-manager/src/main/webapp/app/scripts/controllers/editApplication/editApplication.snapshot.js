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

