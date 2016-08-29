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
    .module('webuiApp.editApplication')
    .component('commandComponent', commandComponent());

    function commandComponent(){
        return {
            templateUrl: 'scripts/components/editApplication/settings/command/editApplication.settings.command.html',
            bindings: {
                application: '<app'
            },
            controller: [
            '$stateParams',
            '$q',
            'ApplicationService',
            'ErrorService',
            commandCtrl
            ],
            controllerAs: 'commandRun',
        }
    }

    function commandCtrl($stateParams, $q, ApplicationService, ErrorService) {

        var commandRun = this;

    ////////////////////////////////////////////////

    function getCommand() {
        // var data = {
        //     applicationName: $stateParams.name,
        //     containerName: vm.myContainer.name,
        // };
        console.log('command get');
        var urlLink = '/{applicationName}/container/{containerName}/command';
        var dir = $resource('volume');

        var volumesList = dir.query().$promise;
        volumesList.then(function(response) {
            editVolume.volumes = response;
        });
    }
}
})();
