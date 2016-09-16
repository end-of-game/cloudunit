/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

'use strict';

angular.module('webuiApp.directives')
.directive('protectinput', ['$timeout', function($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element , attrs ) {
            
            var altPressed = false;
            var shiftPressed = false;
            
            element.bind("keydown", function (ev) {
                var keyCode = ev.keyCode || ev.which;
                if(keyCode == 225) {
                    altPressed = true;
                }
                if(keyCode == 16) {
                    shiftPressed = true;
                }
            });
            
            element.bind("keyup", function (ev) {
                var keyCode = ev.keyCode || ev.which;
                if(keyCode == 225) {
                    altPressed = false;   
                }
                if(keyCode == 16) {
                    shiftPressed = false;
                }
            });
            
            
            element.bind("keydown", function (ev) {
                var keyCode = ev.keyCode || ev.which;
                
                if((keyCode == 56) && altPressed) {
                    return false;
                }
                
                if(((keyCode == 58) && shiftPressed) || ((keyCode == 188) && shiftPressed) || ((keyCode == 191) && shiftPressed)) {
                    return false;
                }
                
                if((keyCode == 170)) {
                    return false;
                }
            });
            
        }
    };
}]);
