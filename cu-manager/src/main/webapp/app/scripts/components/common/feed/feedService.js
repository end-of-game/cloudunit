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
    .module('webuiApp')
    .factory('FeedService', FeedService);

  FeedService.$inject = ['$resource'];

  function FeedService($resource) {

    return {
      listMessages: listMessages,
      listMessagesFirstRows: listMessagesFirstRows,
      listMessagesForCurrentApplication: listMessagesForCurrentApplication
    };


    function listMessages() {
      console.log('ListMessages')
      var logs = $resource('messages/rows/10');
      return logs.query().$promise;
    }

    function listMessagesFirstRows() {
        console.log('ListMessagesFirstRows')
        var logs = $resource('messages/rows');
        return logs.query().$promise;
     }

    // Liste de toutes les images qui sont activés quelque soit leur type
    function listMessagesForCurrentApplication(application) {
      var logs = $resource('messages/application/' + application + '/rows/10');
      return logs.query().$promise;
    }
  }
})();



