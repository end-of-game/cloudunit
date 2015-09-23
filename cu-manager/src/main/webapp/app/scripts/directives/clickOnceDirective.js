'use strict';

angular.module('webuiApp.directives')
.directive('clickOnce', function($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element /*, attrs */) {
            element.bind('click', function() {
                element.attr('disabled', true);
                $timeout(function() {
                    element.attr('disabled', false);
                }, 2000);
            });
        }
    };
});
