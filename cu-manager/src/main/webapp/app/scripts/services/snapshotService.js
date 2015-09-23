(function () {
  'use strict';

  angular
    .module('webuiApp')
    .factory('SnapshotService', SnapshotService);

  SnapshotService.$inject = [
    '$resource',
    '$http'
  ];

  function SnapshotService($resource, $http) {

    return  {
      list: list,
      deleteTag: deleteTag,
      cloneTag: cloneTag,
      create: create
    };

    function list() {
      var SnapshotList = $resource('snapshot/list');
      return SnapshotList.query().$promise;
    }

    // Suppression d'un snapshot
    function deleteTag(tag) {
      return $http.delete('snapshot/' + tag);
    }

    // Suppression d'un snapshot
    function cloneTag(applicationName, tag) {
      var data = {
        applicationName: applicationName,
        tag: tag
      };
      return $http.post('snapshot/clone', data);
    }
    function create(applicationName, snapshot) {
      var data = {
        applicationName: applicationName,
        description: snapshot.description,
        tag: snapshot.name
      };
      return $http.post('snapshot', data);
    }
  }
})();

