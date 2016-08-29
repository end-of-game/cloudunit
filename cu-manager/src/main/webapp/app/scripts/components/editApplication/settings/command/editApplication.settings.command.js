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
            '$resource',
            commandCtrl
            ],
            controllerAs: 'commandRun',
        }
    }


    function commandCtrl($stateParams, $q, ApplicationService, ErrorService, $resource) {
        var commandRun = this;

        commandRun.$onInit = function() {
            getContainers();
            getCommandList();
        }

    ////////////////////////////////////////////////

    function getCommandList() {
        // var data = {
        //     applicationName: $stateParams.name,
        //     containerName: vm.myContainer.name,
        // };
        console.log('command get');
        var urlLink = 'application/newappouf/container/dev-johndoe-newappouf-wildfly-8/command';
        var dir = $resource(urlLink);

        var commandList = dir.query().$promise;
        commandList.then(function(response) {
            console.log(response);
        });
    }

    function getContainers (selectedContainer) {
        var deferred = $q.defer ();
        commandRun.isLoading = true;
        ApplicationService.listContainers ( $stateParams.name )
        .then ( function ( containers ) {
            commandRun.containers = containers;
            commandRun.myContainer = selectedContainer || containers[0];
            commandRun.isLoading = false;
            deferred.resolve ( containers );
        } )
        .catch ( function ( response ) {
            deferred.reject ( response );
        } );
        return deferred.promise;
    }
}
})();
