'use strict';

/**
 * @ngdoc directive
 * @name webuiApp.directive:modalDismiss
 * @description
 * # modalDismiss
 */
angular.module('webuiApp.directives')
    .directive('modaldismiss', [
        '$document',
        function ($document) {
            return {
                link: function (scope, element /*, attrs */) {
                    $document.bind('keyup', function (e) {
                        if (element.hasClass('in')) {
                            if (e.keyCode === 27) {
                                element.modal('hide');
                            }
                        }
                    });
                }
            };
        }
    ]
);
