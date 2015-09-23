'use strict';

angular.module('webuiApp.directives')
    .directive('selectbox', function () {
        return{
            link: function (scope /* , element, attrs */) {
                scope.selectItem = function (item) {
                    scope.selectedItem = item;
                };
            }
        };
    });
