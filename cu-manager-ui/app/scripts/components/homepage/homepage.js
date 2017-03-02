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
        .module ( 'webuiApp.homepage' )
        .component ( 'homepage', Homepage() );

    function Homepage () {
        return {
            templateUrl: 'scripts/components/homepage/homepage.html',
            bindings: {
                context: '='
            },
            controller: [
                'HomepageService',
                'ErrorService',
                HomepageCtrl,
            ],
            controllerAs: 'homepage',
        };
    }

    function HomepageCtrl (HomepageService, ErrorService) {

        var vm;
        vm = this;

        ////////////////////////////////////////////////////

        vm.$onInit = function() {
            HomepageService.listFriends()
                .then(success)
                .catch(error);

            function success(friends) {
                vm.friends = friends;
                return vm.friends;
            }

            function error(response) {
                ErrorService.handle(response);
            }
        }

    }
}) ();
