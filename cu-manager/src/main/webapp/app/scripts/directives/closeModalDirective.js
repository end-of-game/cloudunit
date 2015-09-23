'use strict';

angular.module('webuiApp.directives')
  .directive('closeModal', [
    function () {
      return {
        link: function (scope) {
          scope.$on(':formError', function(e){
            e.preventDefault();
          });
          scope.$on(':formSuccess', function(){
            $('#create-user').modal('hide');
          });
        }
      };
    }
  ]
);
