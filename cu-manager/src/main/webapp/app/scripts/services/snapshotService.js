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

