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
 * @name webuiApp.ModuleService
 * @description
 * # ModuleService
 * Factory in the webuiApp.
 */

angular.module('webuiApp')
    .factory('ModuleService', [
        '$resource',
        function ($resource) {

            var ModuleService = {};
            var Module = $resource('module/:applicationName/:moduleName');

            // Ajout d'un module
            ModuleService.addModule = function (applicationName, imageName) {
                var output = {};
                output.applicationName = applicationName;
                output.imageName = imageName;
                return Module.save(JSON.stringify(output)).$promise;
            };

            // Suppression d'un module
            ModuleService.removeModule = function (applicationName, moduleName) {
                return Module.delete({applicationName: applicationName, moduleName: moduleName});
            };
            return ModuleService;
        }
    ]
);
