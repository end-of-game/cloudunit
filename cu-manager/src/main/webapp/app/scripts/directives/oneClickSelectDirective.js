'use strict';

angular.module('webuiApp.directives')
.directive('oneclickselect', function(){
        return function(scope, element /*, attrs */){
            element.bind('click', function (/* event */) {
                element.focus().select();
            });
        };
    });
