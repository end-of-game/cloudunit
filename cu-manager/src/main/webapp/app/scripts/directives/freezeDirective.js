'use strict';

angular.module('webuiApp.directives')
    .directive('freeze', [
        '$timeout',
        function ($timeout) {
            return {
              scope: true,
                link: function (scope, element) {
                    scope.$on('workInProgress', function (event, data) {
                        element.addClass('pending');
                        $timeout(function () {
                            element.removeClass('pending');
                            scope.$broadcast('workDone');
                        }, data.delay);
                    });
                }
            };
        }
    ]
);

