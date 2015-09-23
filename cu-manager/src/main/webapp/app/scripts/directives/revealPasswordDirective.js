(function(){
  'use strict';

  angular.module('webuiApp.directives')
    .directive('revealPassword', function(){
      return function(scope, element /*, attrs */){
        element.bind('click', function (/* event */) {
          element.parent().toggleClass('show');
          if(element.parent().hasClass('show')){
            element.children().removeClass('eye').addClass('eyeclose');
          } else {
            element.children().removeClass('eyeclose').addClass('eye');
          }
        });
      };
    });
}());
