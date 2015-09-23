'use strict';

angular.module('webuiApp.directives')
    .directive('tooltip', function () {
        return {
            link: function (scope, element, attrs) {
                element.tooltip({
                    title: function(){
                        return attrs.title;
                    },
                    placement: attrs.placement
                });
            }
        };
    });
