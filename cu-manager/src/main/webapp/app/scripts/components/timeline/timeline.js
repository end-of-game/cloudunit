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
    .module ( 'webuiApp.timelines' )
    .directive ( 'timelines', Timelines );


  function Timelines () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/timeline/timeline.html',
      scope: {
        context: '='
      },
      controller: [
        '$scope',
        '$interval',
        'FeedService',
        '$stateParams',
        'ErrorService',
        TimelineCtrl
      ],
      controllerAs: 'timeline',
      bindToController: true
    };
  }

  function TimelineCtrl ( $scope, $interval, FeedService, $stateParams, ErrorService) {

        var timer, currentApp, vm;
        currentApp = $stateParams.name;
        vm = this;

        vm.messages = {};


        init();
        updateMessages();

        timer = $interval(function () {
          vm.context === 'dashboard' ? updateMessages() : updateMessagesForCurrentApplication();
        }, 2000);


        $scope.$on('$destroy', function () {
          $interval.cancel(timer);
        });

        ////////////////////////////////////////////////////

        function init() {
          vm.context === 'dashboard' ? updateMessages() : updateMessagesForCurrentApplication();
        }

        function updateMessages() {
          FeedService.listMessages()
            .then(success)
            .catch(error);

          function success(messages) {
            console.log(messages);
            $scope.messages = messages;
            return vm.messages;
          }

          function error(response) {
            ErrorService.handle(response);
            if(timer){
              $interval.cancel(timer);
            }
          }
        }

        function updateMessagesForCurrentApplication() {
          if(!currentApp){
            return;
          }
          FeedService.listMessagesForCurrentApplication(currentApp)
            .then(success)
            .catch(error);

          function success(messages) {
            vm.messages = messages;
            return vm.messages;
          }

          function error(response) {
            FeedService.handle(response);
            if(timer){
              $interval.cancel(timer);
            }
          }
        }
  }
}) ();

