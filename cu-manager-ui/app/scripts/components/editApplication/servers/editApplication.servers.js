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
    .module ( 'webuiApp.editApplication' )
    .component ( 'editAppServers', Modules() );

    function Modules () {
        return {
            templateUrl: 'scripts/components/editApplication/servers/editApplication.servers.html',
            bindings: {
                app: '='
            },
            controller: [
            '$rootScope',
            'ImageService',
            'ModuleService',
            '$stateParams',
            ModulesCtrl
            ],
            controllerAs: 'servers',
        };
    }

    function ModulesCtrl ( $rootScope, ImageService, ModuleService, $stateParams) {
        var sm = this;
        sm.serversList = [];
        sm.serversCategorie = [];
        sm.typeImage = $stateParams.typeImage;

        sm.$onInit = function() {
            ImageService.findEnabledServer().then(function(response) {
                sm.serversList = response;

                if(sm.typeImage === '') {
                    // @TODO foreach
                    console.log("Launch servers..", sm.serversList);

                    for(var i=0 ; i < sm.serversList.length - 1 ; i++) {
                        if (!(sm.serversCategorie.indexOf(sm.serversList[i].prefixEnv) != -1 ||
                            sm.serversList[i].prefixEnv == undefined)) {
                                sm.serversCategorie.push(sm.serversList[i].prefixEnv);
                        }
                    }

                    console.log(sm.serversCategorie);
                }
            });
        }
    }
}) ();
