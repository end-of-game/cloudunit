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

/**
 * @ngdoc filter
 * @name webuiApp.filter:truncatestring
 * @function
 * @description
 * # truncatestring
 * Filter in the webuiApp.
 */
angular.module('webuiApp')
    .filter('appByServerImage', function () {
        return function (input, serverImageName) {
            if(serverImageName) {
                var res = [];
                input.forEach(function(element, index) {
                    if(element.servers) {
                        if(element.servers[0].image.prefixEnv === serverImageName) {
                            res.push(element);
                        }    
                    }
                });
                return res;    
            }
            return input;
        };
    });
