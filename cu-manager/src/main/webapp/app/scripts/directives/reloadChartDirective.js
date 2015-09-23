(function(){
  'use strict';

  angular.module('webuiApp.directives')
    .directive('reloadChart', function(){
      return {
        scope: {
          media: '@'
        },
        link: function(scope, element /*attrs*/){
          // reload iframe when selected container changes
          scope.$watch('media', function () {
            $(element).attr('src', $(element).attr('src'));
          });
        }
      };
    });
}());
