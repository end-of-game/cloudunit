'use strict';

/**
 * @ngdoc filter
 * @name webuiApp.filter:converttobool
 * @function
 * @description
 * # converttobool
 * Filter in the webuiApp.
 */
angular.module('webuiApp')
    .filter('stringtobooleanfilter', function () {
        return function (input) {
            return input === 'false';
        };
    });
