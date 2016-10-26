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
    .module ( 'webuiApp.editApplication' )
    .component ( 'editAppSnapshot', Snapshot() );

  function Snapshot(){
    return {
      templateUrl: 'scripts/components/editApplication/snapshots/editApplication.snapshot.html',
      bindings: {
        app: '='
      },
      controller: [
        'SnapshotService',
        SnapshotCtrl
      ],
      controllerAs: 'snapshot',
    };
  }

  function SnapshotCtrl ( SnapshotService ) {

    var vm = this;
    vm.message = "";
    vm.newSnapshot = {
      name: '',
      description: '',
      tags: []
    };

    vm.createNewSnapShot = createNewSnapShot;
    vm.removeTag = removeTag;
    vm.addTag = addTag;

    // Creation for a new snapshot
    function createNewSnapShot ( applicationName, snapshot ) {
      SnapshotService.create ( applicationName, snapshot )
        .then ( success )
        .catch ( error );

      function success () {
        // reset form
        vm.newSnapshot.name = '';
        vm.newSnapshot.description = 'Enter a comment';
        vm.newSnapshot.tags = [];
        vm.selectedTag = -1;
      }

      function error ( response ) {
        if ( response.data.message === 'You can\'t create a snapshot if your app is pending' ) {
          return;
        }
        vm.message = response.data.message;
      }
    }

    function removeTag ( event, index ) {
      if ( typeof index === 'number' ) {
        vm.newSnapshot.tags.splice ( index, 1 );
      }
    }

    function addTag ( event, tag ) {
      if ( tag && vm.newSnapshot.tags.indexOf ( tag ) === -1 ) {
        vm.newSnapshot.tags.push ( tag );
      }
    }
  }
}) ();

