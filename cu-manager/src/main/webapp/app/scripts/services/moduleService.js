'use strict';

/**
 * @ngdoc service
 * @name webuiApp.moduleservice
 * @description
 * # moduleservice
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
                return Module.save(JSON.stringify(output));
            };

            // Suppression d'un module
            ModuleService.removeModule = function (applicationName, moduleName) {
                return Module.delete({applicationName: applicationName, moduleName: moduleName});
            };
            return ModuleService;
        }
    ]
);
