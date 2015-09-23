'use strict';

/**
 * @ngdoc directive
 * @name webuiApp.directive:showTab
 * @description
 * # showTab
 */
angular.module('webuiApp.directives')
    .directive('showtab',
    function () {
        return {
            link: function (scope, element, attrs) {
                element.click(function (e) {
                    var target = attrs.href;
                    var link = $('li > a[href=' + target + ']');
                    var menuItems = $('.menu-group > li');

                    menuItems.removeClass('active');
                    $(element).tab('show');
                    link.parent().addClass('active');

                    e.preventDefault();
                });

            }
        };
    });
