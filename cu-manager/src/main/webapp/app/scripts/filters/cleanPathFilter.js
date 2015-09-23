'use strict';

/**
 * @ngdoc filter
 * @name webuiApp.filter:truncatestring
 * @function
 * @description
 * # truncatestring
 * Filter in the webuiApp.
 */
angular.module('webuiApp')
    .filter('cleanPath', function () {
        return function (input) {
            return input.replace('/', '');
        };
    });
