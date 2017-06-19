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
   * @name webuiApp.controller:TimelineCtrl
   * @description
   * # TimelineCtrl
   * Controller of the webuiApp
   */
  angular
    .module ( 'webuiApp.mainTimeline' )
    .component ( 'mainTimeline', MainTimeline() );


  function MainTimeline () {
    return {
      templateUrl: 'scripts/components/mainTimeline/mainTimeline.html',
      bindings: {
        context: '='
      },
      controller: [
        'FeedService',
        'ErrorService',
        TimelineCtrl
      ],
      controllerAs: 'timeline',
    };
  }

  function TimelineCtrl (FeedService, ErrorService) {

    var vm;
    vm = this;

    vm.pageSize = 10;
    vm.currentPage = 1;
    vm.event = '';
    vm.date = 'recent';
    vm.orderByDate = true;
    vm.applicationName = '';

    ////////////////////////////////////////////////////

    vm.$onInit = function() {
      FeedService.listMessagesFirstRows()
        .then(success)
        .catch(error);

      function success(messages) {
        vm.messages = messages;
        return vm.messages;
      }

      function error(response) {
        ErrorService.handle(response);
        if(timer){
          $interval.cancel(timer);
        }
      }
    }

  }
}) ();

