'use strict';

/**
 * @ngdoc service
 * @name webuiApp.ServerService
 * @description
 * Factory in the webuiApp.
 */
angular.module('webuiApp')
    .factory('ServerService', [
        '$resource',
        function ($resource) {

            var ServerService = {};

            // Liste de toutes les images qui sont activés quelque soit leur type
            ServerService.saveConfigurationJVM = function (applicationName, jvmMemory, jvmOptions, jvmRelease) {
                var options = $resource('server/configuration/jvm',
                    {},
                    { 'update': { method: 'PUT' }
                    });
                return options.update({applicationName: applicationName, jvmMemory: jvmMemory, jvmOptions: jvmOptions, jvmRelease: jvmRelease, location:'webui'});
            };

            return ServerService;
        }
    ]
);

