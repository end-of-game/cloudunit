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

'use strict';

/**
 * @ngdoc service
 * @name webuiApp.JVMService
 * @description
 * Factory in the webuiApp.
 */
angular.module('webuiApp')
    .factory('JVMService', [
        '$resource',
        function ($resource) {

            var JVMService = {};

            // Liste de toutes les images qui sont activés quelque soit leur type
            JVMService.saveConfigurationJVM = function (applicationName, jvmMemory, jvmOptions, jvmRelease) {
                var options = $resource('server/configuration/jvm',
                    {},
                    { 'update': { method: 'PUT' }
                    });
                return options.update({applicationName: applicationName, jvmMemory: jvmMemory, jvmOptions: jvmOptions, jvmRelease: jvmRelease, location:'webui'});
            };

            return JVMService;
        }
    ]
);

