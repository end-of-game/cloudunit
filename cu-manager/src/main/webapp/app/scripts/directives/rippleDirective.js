'use strict';

angular.module('webuiApp.directives')
    .directive('ripple', function () {

        var ripple = {
            init: function (element, e) {
                var child, ink, d, x, y;
                child = element.children();
                //create .ink element if it doesn't exist
                if (child.find('.ink').length === 0){
                    child.prepend('<span class="ink"></span>');
                }


                ink = child.find('.ink');
                //incase of quick double clicks stop the previous animation
                ink.removeClass('animate');

                //set size of .ink
                if (!ink.height() && !ink.width()) {
                    //use child's width or height whichever is larger for the diameter to make a circle which can cover the entire element.
                    d = Math.max(child.outerWidth(), child.outerHeight());
                    ink.css({height: d, width: d});
                }

                //get click coordinates
                //logic = click coordinates relative to page - child's position relative to page - half of self height/width to make it controllable from the center;
                x = e.pageX - child.offset().left - ink.width() / 2;
                y = e.pageY - child.offset().top - ink.height() / 2;

                //set the position and add class .animate
                ink.css({top: y + 'px', left: x + 'px'}).addClass('animate');
            }
        };
        return function (scope, element /*, attrs */) {
            element.bind('click', function (e) {
                ripple.init(element, e);
            });
        };
    });
