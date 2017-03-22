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
 * @ngdoc service
 * @name webuiApp.ModuleService
 * @description
 * # ModuleService
 * Factory in the webuiApp.
 */

angular
    .module('webuiApp')
    .factory('ModuleService', ModuleService);

    ModuleService.$inject = [
        '$resource',
        'traverson'
    ]
    
    function ModuleService($resource, traverson) {

        var Module = $resource('module/:applicationName/:moduleName');

        traverson.registerMediaType(TraversonJsonHalAdapter.mediaType, TraversonJsonHalAdapter);

        var traversonService = traverson
            .from('/applications')
            .jsonHal()
            .withRequestOptions({ headers: { 'Content-Type': 'application/hal+json'} });
        
        return {
            addModule: addModule,
            removeModule: removeModule,
            addService: addService
        };

        // Ajout d'un service
        function addService(applicationName, serviceName) {

            return traversonService
                .newRequest()
                .follow('applicationResourceList[name:' + applicationName + ']', 'cu:services')
                .post({
                    name: serviceName
                })
                .result;
        };

        // Ajout d'un module
        function addModule(applicationName, moduleName) {

            return traversonService
                .newRequest()
                .follow('applicationResourceList[name:' + applicationName + ']', 'modules')
                .post({
                    name: moduleName
                })
                .result;
            // var output = {};
            // output.applicationName = applicationName;
            // output.imageName = imageName;
            // return Module.save(JSON.stringify(output)).$promise;
        };

        // Suppression d'un module
        function removeModule (applicationName, moduleName) {

            return traversonService
                .newRequest()
                .follow('applicationResourceList[name:' + applicationName + ']', 'modules', 'moduleResourceList[name:' + imageName + ']')
                .delete()
                .result;

            // return Module.delete({applicationName: applicationName, moduleName: moduleName}).$promise;
        };
    }

}) ();