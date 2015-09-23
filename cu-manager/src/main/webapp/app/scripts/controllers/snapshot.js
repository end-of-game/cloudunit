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

