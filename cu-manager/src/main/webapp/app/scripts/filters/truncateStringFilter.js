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
    .filter('truncatestringfilter', function () {
        return function (input) {
            var raw = input.split('-');
            return raw[0];
        };
    });
