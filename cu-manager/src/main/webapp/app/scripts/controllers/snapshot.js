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

  /**
   * @ngdoc function
   * @name webuiApp.controller:SnapshotCtrl
   * @description
   * # SnapshotCtrl
   * Controller of the webuiApp
   */
  angular
    .module('webuiApp')
    .controller('SnapshotCtrl', SnapshotCtrl);

  SnapshotCtrl.$inject = [
    'SnapshotService',
    'ApplicationService',
    '$filter'
  ];

  function SnapshotCtrl(SnapshotService, ApplicationService, $filter) {
    var vm = this;
    vm.snapshots = [];
    vm.deleteTag = deleteTag;
    vm.cloneTag = cloneTag;
    vm.isValid = isValid;
    vm.applicationName = '';
    vm.message = '';
    vm.resetForm = resetForm;
    vm.errorMsg = false;

    init();

    ////////////////////////////////////////////

    function init() {
      SnapshotService.list()
        .then(success);

      function success(snapshots) {
        var decoratedSnapshots = [];
        angular.forEach(snapshots, function (snap) {
          snap.calendarDate = $filter('date')(snap.date, 'mediumDate');
          decoratedSnapshots.push(snap);
        });
        vm.snapshots = decoratedSnapshots;
      }
    }


    function cloneTag(snapshot) {
      SnapshotService.cloneTag(vm.applicationName, snapshot.tag)
        .then(success)
        .catch(error);

      function success() {
        init();
        resetForm();
      }

      function error(response){
        vm.errorMsg = response.data.message;
      }
    }

    function deleteTag(tag) {
      SnapshotService.deleteTag(tag)
        .then(success)
        .catch(error);

      function success() {
        init();
      }

      function error(response){
        vm.errorMsg = response.data.message;
      }
    }

    function isValid(applicationName, serverName) {
      ApplicationService.isValid(applicationName, serverName)
        .then(success)
        .catch(error);

      function success() {
        vm.message = '';
      }

      function error(response) {
        vm.message = response.data.message;
      }
    }

    function resetForm(){
      vm.cloneAppForm.$setPristine();
      vm.applicationName = '';
      vm.message = '';
    }
  }
})();

